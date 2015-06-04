package edu.columbia.ldpd.text.nny.impl;

import edu.columbia.ldpd.text.nny.NNYRecord;

import org.apache.solr.common.SolrInputDocument;

import edu.columbia.ldpd.text.Configuration;
import edu.columbia.ldpd.text.IndexingException;
import edu.columbia.ldpd.text.fields.IndexTypes.Multiple;
import edu.columbia.ldpd.text.fields.IndexTypes.Store;
import edu.columbia.ldpd.text.fields.IndexTypes.Tokenize;
import edu.columbia.ldpd.text.impl.BasePageTransformer;

public class SolrInputDocTransformer extends BasePageTransformer<NNYRecord, SolrInputDocument> {
    private Configuration config;
    public SolrInputDocTransformer(Configuration config) {
        this.config = config;
    }
	@Override
	public SolrInputDocument transform(NNYRecord page)
			throws IndexingException {
        SolrInputDocument doc = new SolrInputDocument();
		
	    if (page.getSessionNum() != null) 
	        doc.addField(fieldName("session_num",Store.YES,Multiple.NO,Tokenize.NO), page.getSessionNum());

 	    if (page.getYear() != null)
	        doc.addField(fieldName("year",Store.YES,Multiple.NO,Tokenize.NO), page.getYear());
        doc.addField(fieldName("target_filename",Store.YES,Multiple.NO,Tokenize.NO),
                page.getTargetFileName());
        //System.out.println( page.getTargetFileName() );
        doc.addField(fieldName("basename",Store.YES,Multiple.NO,Tokenize.NO), page.getBaseName());
        doc.addField(fieldName("title",Store.YES,Multiple.NO,Tokenize.NO), page.getTitle());
        doc.addField(fieldName("author",Store.YES,Multiple.NO,Tokenize.NO), page.getInterviewee());
        doc.addField(fieldName("page_id",Store.YES,Multiple.NO,Tokenize.NO), page.getPageId());
        doc.addField("id", idFor(page));
        doc.addField(fieldName("page_num",Store.YES,Multiple.NO,Tokenize.NO), page.getPageNum());
        doc.addField(fieldName("interview_num",Store.YES,Multiple.NO,Tokenize.NO), page.getInterviewNum());
        doc.addField(fieldName("text",Store.YES,Multiple.NO,Tokenize.YES), page.getText());

        // Synthetic fields in the lucene index
		String intervieweeRootUrl = config.urlPrefix() + "/" + page.getBaseName();
        String transcriptUrl      = intervieweeRootUrl + "/transcripts/" + page.getTargetFileName();

        doc.addField(fieldName("url",Store.YES,Multiple.NO,Tokenize.NO), transcriptUrl);

        // These fields do not have to be stored in order to sort by them,
        // but for debugging purposes we'll want to have access to it.
        String sortAuthor = sortable(page.getAuthor());
        doc.addField(fieldName("sort_author",Store.YES,Multiple.NO,Tokenize.NO), sortAuthor);

        String sortTitle = sortable(page.getTitleSort());
        doc.addField(fieldName("sort_title",Store.YES,Multiple.NO,Tokenize.NO), sortTitle);

        doc.addField(fieldName("doc_id",Store.YES,Multiple.NO,Tokenize.NO), docIdFor(page));

        doc.addField(
            fieldName("collection",Store.YES,Multiple.YES,Tokenize.NO), new String[]{"nny"});

        // debug...
        //System.out.println("id=" + id);
        //System.out.println("collection=" + collection);

        // For simpler searching, concatenate all fields together
        // (Not actually ALL fields, only those a person might search.)
        String[] allFields = {page.getText(),
                           page.getTitle(),
                           page.getInterviewee(),
                           page.getInterviewer()};
        doc.addField("all_text_timv", allFields);
        return doc;
	}

}
