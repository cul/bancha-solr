package edu.columbia.ldpd.text.bancha;

import java.io.File;
import java.util.Hashtable;
import java.util.concurrent.Callable;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.digester.Digester;

import edu.columbia.ldpd.text.Configuration;
import edu.columbia.ldpd.text.PageProcessor;
import edu.columbia.ldpd.text.TextIndexer;
import edu.columbia.ldpd.text.bancha.impl.SolrPageProcessor;
import edu.columbia.ldpd.text.bancha.sax.CollectionIdHandler;
import edu.columbia.ldpd.text.impl.Yield;

public class IndexCallable implements Callable<TextIndexer> {

	private final Configuration config;
	public IndexCallable() {
		this(new Configuration());
	}

	public IndexCallable(Configuration config) {
		this.config = config;
	}

	@Override
	public TextIndexer call() throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = null;
        try {
            // Use the default (non-validating) parser
            parser = factory.newSAXParser();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        BuildIndex indexer=  new BuildIndex(config, parser);

        indexer.run(config, parser);
        indexer.cleanUp();
        return indexer;
	}

	static Hashtable<String,String> buildIdToCollectionHash(Configuration config, SAXParser saxParser) {
	
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

	protected static Digester getDigester(final PageProcessor<BanchaPage> proc) {
	    // Create digester, set global parameters
	    Digester digester = new Digester();
	    digester.setValidating(false);
	    digester.push( proc );
	
	    // Specify actions for each XML tag
	    Yield<PageProcessor<BanchaPage>> yield = Yield.wrap(proc);
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

	protected static PageProcessor<BanchaPage> getProcessor(Configuration config, SAXParser parser) {
	    PageProcessor<BanchaPage> proc = null;
	    try {
	        // Find out which bib ids are members of which book collections
	        Hashtable<String,String> idToCollectionHash =
	                IndexCallable.buildIdToCollectionHash(config, parser);
	        proc = new SolrPageProcessor(config, idToCollectionHash);
	    } catch (Exception e) {
	        System.out.println(e.getMessage());
	        System.exit(-1);
	    }
	    return proc;
	}

}
