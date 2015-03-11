package bancha;
// Core java classes
import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.util.*;

// high-level interface to SAX processing
import org.apache.commons.digester.Digester;

import bancha.impl.SolrPageProcessor;
import bancha.impl.Yield;
import bancha.sax.AudienceHandler;
import bancha.sax.CollectionIdHandler;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;

public class BuildIndex {

    public static String banchaDir = "/e/ldpd/projects/bancha";

    private final Digester digester;

    public static void main (String[] args) {

        // argument processing
        if (args.length < 2) {
            usage();
            System.exit(-1);
        }
        String environment = args[0];
        if ( ! environment.equals("dev") &&
                ! environment.equals("test") &&
                ! environment.equals("prod") ) {
            usage();
            System.exit(-1);
        }

        Configuration config = null;
        try {
            config = new Configuration(environment, args[1]);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }

        // Read xml files from the TEI directory, index each one
        File dir = null;
        DirectoryStream<Path> files = null;
        try {
            dir = config.teiDir();
            files = config.teiFiles();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = null;
        PageProcessor proc = null;
        try {
            // Use the default (non-validating) parser
            parser = factory.newSAXParser();
            // Find out which bib ids are members of which book collections
            Hashtable<String,String> idToCollectionHash =
                    buildIdToCollectionHash(config, parser);
            proc = new SolrPageProcessor(config, idToCollectionHash);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        BuildIndex indexer=  new BuildIndex(proc);
        AudienceHandler audience = new AudienceHandler();
        Iterator<Path> paths = files.iterator();
        while (paths.hasNext()) {
        	Path path = paths.next();
            String xmlfile = (path.isAbsolute()) ? path.toString() : dir + "/" + path.toString();

            // Skip any files we don't want to index.
            // XML files only.
            if (! xmlfile.endsWith(".xml") ) continue;

            // Skip any files marked AUDIENCE=internal in their errata
            // files - these are not to be searchable through lucene.
            try {
                if (errataAudienceIsInternal(parser, audience,config.errataDir(), xmlfile)) continue;
            } catch (FileNotFoundException e) {
                System.out.println(e.getMessage());
                System.exit(-1);
            }

            // Indexing each TEI XML file
            try {
                indexer.indexDoc(xmlfile);
            } catch (Exception e) {
                System.err.println("Fatal error in indexDoc(" + xmlfile + ")" );
                System.err.println(e.getClass() + ": " + e.getMessage());
                System.exit(-1);
            }
        }


        // We've indexed all documents - optimize the Solr index
        try {
        	files.close();
            proc.cleanUp();
        } catch (Exception e) {
            System.err.println("Fatal error optimizing/closing index");
            System.err.println(e.getClass() + ": " + e.getMessage());
            System.exit(-1);
        }
    }

    public BuildIndex(Digester digester) {
        this.digester = digester;
    }

    public BuildIndex(PageProcessor proc) {
        this.digester = getDigester(proc);
    }

    protected static Digester getDigester(final PageProcessor proc) {
        // Create digester, set global parameters
        Digester digester = new Digester();
        digester.setValidating(false);
        digester.push( proc );

        // Specify actions for each XML tag
        Yield<PageProcessor> yield = Yield.wrap(proc);
        digester.addRule("book", yield);
        digester.addObjectCreate("book/page", BanchaPage.class );

        digester.addCallMethod("book/page/target_filename", "setTargetFileName", 0);
        digester.addCallMethod("book/page/basename", "setBaseName", 0);
        digester.addCallMethod("book/page/title", "setTitle", 0);
        digester.addCallMethod("book/page/title_sort", "setTitleSort", 0);
        digester.addCallMethod("book/page/author", "setAuthor", 0);
        digester.addCallMethod("book/page/page_id", "setPageId", 0);
        digester.addCallMethod("book/page/page_num", "setPageNum", 0);
        digester.addCallMethod("book/page/imprint", "setImprint", 0);
        digester.addCallMethod("book/page/url_label", "setUrlLabel", 0);
        digester.addCallMethod("book/page/text", "setText", 0);

        // call addBookPage() when the next 'page' pattern is seen
        digester.addSetNext("book/page", "processPage" );
        return digester;
    }

    protected void indexDoc (String xmlfile) {

        System.out.println("Indexing " + xmlfile);

        // Parse the TEI XML doc using Digester (SAX wrapper)
        try {
            this.digester.parse(xmlfile);
        } catch (Exception e) {
            System.err.println("Exception in digester.parse(" + xmlfile + ")" );
            System.err.println(e.getClass() + ": " + e.getMessage() );
        }
    }

    public static void usage () {
        System.out.println("usage:  BuildIndex [ prod | test | dev ] $PROPERTIES");
    }


    private static Hashtable<String,String> buildIdToCollectionHash(Configuration config, SAXParser saxParser) {

        String [] collectionFiles = config.collections();
        Hashtable<String,String> result = new Hashtable<>();
        CollectionIdHandler handler = new CollectionIdHandler("", result);

        for (int i = 0; i < collectionFiles.length; i++) {
            String collectionFilename = collectionFiles[i];

            String collectionCode = collectionFilename;
            collectionCode = collectionCode.replaceAll("/.*/", "");
            collectionCode = collectionCode.replaceAll("_mets.xml", "");

            System.out.println("Parsing collection code " + collectionCode + " from file " + collectionFilename);

            handler.setCollectionId(collectionCode);

            try {
                // Parse the input
                saxParser.parse( new File(collectionFilename), handler );
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        return result;
    }

    public static boolean errataAudienceIsInternal(
            SAXParser saxParser,
            AudienceHandler handler,
            File errataDir, 
            String xmlFile) 
    {
        boolean isInternal = true;   // Default to private

        // Derive the filename of the errata file
        String errataPath = xmlFile;
        errataPath = errataPath.replaceAll("/.*/", "");
        errataPath = errataPath.replaceAll("_tei.xml", ".xml");
        File errataFile = new File(errataDir, errataPath);

        try {
            // Parse the errata file
            saxParser.parse( errataFile, handler );
        } catch (Throwable t) {
            t.printStackTrace();
        }

        if (handler.lastAudienceValue().equals("internal")) {
            isInternal = true;
        } else if (handler.lastAudienceValue().equals("external")) {
            isInternal = false;
        } else {
            System.err.println("Found unexpected AUDIENCE value '" + handler.lastAudienceValue() + "' in errata file " + errataFile);
            System.exit(1);
        }

        return isInternal;
    }

}


