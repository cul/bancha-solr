package edu.columbia.ldpd.text.bancha;
// Core java classes
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;


// high-level interface to SAX processing
import org.apache.commons.digester.Digester;

import edu.columbia.ldpd.text.Configuration;
import edu.columbia.ldpd.text.IndexingException;
import edu.columbia.ldpd.text.PageProcessor;
import edu.columbia.ldpd.text.impl.AbstractTextIndexer;
import edu.columbia.ldpd.text.bancha.sax.AudienceHandler;

import javax.xml.parsers.SAXParser;

public class BuildIndex extends AbstractTextIndexer {

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

        try {
			new IndexCallable(config).call();
		} catch (Exception e) {
            System.err.println("Fatal error on index file handling");
            System.err.println(e.getClass() + ": " + e.getMessage());
            System.exit(-1);
		}
    }

    private final PageProcessor<BanchaPage> proc;

    public BuildIndex(PageProcessor<BanchaPage> proc, Digester digester) {
        super(digester);
        this.proc = proc;
    }

    public BuildIndex(PageProcessor<BanchaPage> proc) {
        this(proc, IndexCallable.getDigester(proc));
    }

    public BuildIndex(Configuration config, SAXParser parser) {
        this(IndexCallable.getProcessor(config, parser));
    }

    @Override
    public void run(Configuration config, SAXParser parser) {
        // Read xml files from the TEI directory, index each one
        File dir = null;
        Iterable<Path> files = null;
        try {
            dir = config.teiDir();
            files = config.teiFiles();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }

        AudienceHandler audience = new AudienceHandler();
        for (Path path : files) {
            String xmlPath = (path.isAbsolute()) ? path.toString() : dir + "/" + path.toString();

            // Skip any files we don't want to index.
            // XML files only.
            if (! xmlPath.endsWith(".xml") ) continue;

            // Skip any files marked AUDIENCE=internal in their errata
            // files - these are not to be searchable through lucene.
            try {
                if (errataAudienceIsInternal(parser, audience,config.errataDir(), xmlPath)) continue;
            } catch (FileNotFoundException e) {
                System.out.println(e.getMessage());
                System.exit(-1);
            }

            // Indexing each TEI XML file
            try {
                this.indexXml(xmlPath);
            } catch (Exception e) {
                System.err.println("Fatal error in indexDoc(" + xmlPath + ")" );
                System.err.println(e.getClass() + ": " + e.getMessage());
                System.exit(-1);
            }
        }


    }

    @Override
    public void cleanUp() throws IndexingException {
    	this.proc.cleanUp();
    }

    public static void usage () {
        System.out.println("usage:  BuildIndex [ prod | test | dev ] $PROPERTIES");
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


