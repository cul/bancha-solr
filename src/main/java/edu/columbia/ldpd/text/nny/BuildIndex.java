package edu.columbia.ldpd.text.nny;
/*
 * Indexer
 * 
 * RCS information: $Id: Indexer.java,v 1.9 2005/10/06 14:45:59 da217 Exp da217 $
 *
 * This software is based on DigesterMarriesLucene by Otis Gospodnetic
 * http://www-128.ibm.com/developerworks/library/j-lucene/
 *
 */

import java.io.IOException;
import java.nio.file.Path;

import javax.xml.parsers.SAXParser;

import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

import edu.columbia.ldpd.text.Configuration;
import edu.columbia.ldpd.text.IndexingException;
import edu.columbia.ldpd.text.PageProcessor;
import edu.columbia.ldpd.text.impl.AbstractTextIndexer;
import edu.columbia.ldpd.text.impl.Yield;

/**
 * Parses NNY XML file and creates or updates Lucene index.  Updates are accomplished
 * by deleting documents based on institution name and then appending new records.
 * [ TODO add documentation on search params ]
 */
public class BuildIndex extends AbstractTextIndexer {

    public static String indexDir = "index";  // can be overridden by command line option

	final static char OPTION_DELETE_FIELD           = 'f';
	final static char OPTION_DELETE_INSTITUTION     = 'i';
	final static char OPTION_DELETE_TEXT            = 't';
	final static char OPTION_INDEX_DIRECTORY        = 'd';
	final static char OPTION_APPEND                 = 'a';

    public static void usage() {
		
	}

	/**
     * Created an index to add eresourceRecords to, configures Digester rules and
     * actions, parses the XML file specified as the first argument.
     *
     * @param args command line arguments
     */
    public static void main (String[] args) throws IOException, SAXException {

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

    public BuildIndex(Digester digester) {
    	super(digester);
    }

    public BuildIndex(PageProcessor<NNYRecord> processor) {
    	super(getDigester(processor));
    }

    protected static Digester getDigester(final PageProcessor<NNYRecord> proc) {
        // Create digester, set global parameters
        Digester digester = new Digester();
        digester.setValidating(false);
        digester.push( proc );

        // Specify actions for each XML tag
        Yield<PageProcessor<NNYRecord>> yield = Yield.wrap(proc);
        digester.addRule("nny", yield);
        digester.addObjectCreate("nny/page", NNYRecord.class );

        // set properties of NNYRecord instance using specified methods
        digester.addCallMethod("nny/page/target_filename", "setTargetFileName", 0);
        digester.addCallMethod("nny/page/basename", "setBaseName", 0);
        digester.addCallMethod("nny/page/interview_num", "setInterviewNum", 0);
        digester.addCallMethod("nny/page/session_num", "setSessionNum", 0);
        digester.addCallMethod("nny/page/page_id", "setPageId", 0);
        digester.addCallMethod("nny/page/interviewer", "setInterviewer", 0);
        digester.addCallMethod("nny/page/interviewee", "setInterviewee", 0);
        digester.addCallMethod("nny/page/page_num", "setPageNum", 0);
        digester.addCallMethod("nny/page/text", "setText", 0);
        digester.addCallMethod("nny/page/year", "setYear", 0);

        // call addNNYRecord() when the next 'page' pattern is seen
        digester.addSetNext("nny/page", "processPage" );
        return digester;
    }

	@Override
	public void run(Configuration config, SAXParser parser) throws IndexingException {

		int numIndexedRecords = 0;
	    int numFailedRecords = 0;
	    try {
		for (Path xmlPath: config.xmlFiles()) {
            // Skip any files we don't want to index.
            // XML files only.
            if (! xmlPath.toString().endsWith(".xml") ) continue;
	    	try {
	    		this.indexXml(xmlPath.toString());
	    		numIndexedRecords++;
	    	} catch (Exception e) {
	    		numFailedRecords++;
                System.err.println("Failed: " + xmlPath + "...");	    		
                System.err.println(e.getMessage());	    		
	    	}
    	}
	    } catch (IOException e) {
	    	throw new IndexingException(e.getMessage(),e);
	    }
        System.out.println(numIndexedRecords + " records successfully indexed.");
        if (numFailedRecords > 0) {
        	System.out.println(numFailedRecords + " records were not indexed due to errors.");
        }
	}

}
