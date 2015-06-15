package edu.columbia.ldpd.text.nny.impl;

import edu.columbia.ldpd.text.nny.NNYRecord;

import org.apache.solr.common.SolrInputDocument;

import edu.columbia.ldpd.text.Configuration;
import edu.columbia.ldpd.text.IndexingException;
import edu.columbia.ldpd.text.fields.IndexTypes.Multiple;
import edu.columbia.ldpd.text.fields.IndexTypes.Store;
import edu.columbia.ldpd.text.fields.IndexTypes.Tokenize;
import edu.columbia.ldpd.text.impl.BasePageTransformer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SolrInputDocTransformer extends BasePageTransformer<NNYRecord, SolrInputDocument> {
    private Configuration config;
    private Pattern id_pattern = Pattern.compile("\\w+(_\\d+){2}_[a-z0-9]+");
    private final String sessionNumField;
    private final String yearField;
    private final String interviewNumField;

    public SolrInputDocTransformer(Configuration config) {
        this.config = config;
        this.sessionNumField = fieldName("session_num",Store.YES,Multiple.NO,Tokenize.NO);
        this.yearField = fieldName("year",Store.YES,Multiple.NO,Tokenize.NO);
        this.interviewNumField = fieldName("interview_num",Store.YES,Multiple.NO,Tokenize.NO);
    }

    @Override
    public String docIdFor(NNYRecord page) {
        String id = page.getTargetFileName();
        Matcher m = id_pattern.matcher(id);
        if (!m.find()) {
            System.err.println("Cannot parse id from \"" + id + "\"");
            throw new IllegalArgumentException("Cannot parse id from \"" + id + "\"");
        }
        id = m.group(0);
        return id;
    }
	@Override
	public SolrInputDocument transform(NNYRecord page)
			throws IndexingException {
        SolrInputDocument doc = new SolrInputDocument();
		
        doc.addField(fileNameField, page.getTargetFileName());
        doc.addField(baseNameField, page.getBaseName());
        doc.addField(titleField, page.getTitle());
        doc.addField(authorField, page.getInterviewee());
        doc.addField(pageIdField, page.getPageId());
        doc.addField(ID_FIELD, idFor(page));
        doc.addField(pageNumField, page.getPageNum());
        doc.addField(textField, page.getText());

        doc.addField(urlField, page.getUrl(config));

        // These fields do not have to be stored in order to sort by them,
        // but for debugging purposes we'll want to have access to it.
        String sortAuthor = sortable(page.getAuthor());
        doc.addField(sortAuthorField, sortAuthor);

        String sortTitle = sortable(page.getTitleSort());
        doc.addField(sortTitleField, sortTitle);

        doc.addField(docIdField, docIdFor(page));

        doc.addField(collectionField, new String[]{"nny"});

        // debug...
        //System.out.println("id=" + id);
        //System.out.println("collection=" + collection);

        // For simpler searching, concatenate all fields together
        // (Not actually ALL fields, only those a person might search.)
        String[] allFields = {page.getText(),
                           page.getTitle(),
                           page.getInterviewee(),
                           page.getInterviewer()};
        doc.addField(ALL_TEXT_FIELD, allFields);

        // locally defined fields
        if (page.getSessionNum() != null) 
            doc.addField(sessionNumField, page.getSessionNum());

        if (page.getYear() != null)
            doc.addField(yearField, page.getYear());
        doc.addField(interviewNumField, page.getInterviewNum());
        return doc;
	}

}
