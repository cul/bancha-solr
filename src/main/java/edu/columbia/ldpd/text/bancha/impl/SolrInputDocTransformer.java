package edu.columbia.ldpd.text.bancha.impl;

import java.util.Hashtable;

import org.apache.solr.common.SolrInputDocument;

import edu.columbia.ldpd.text.bancha.BanchaException;
import edu.columbia.ldpd.text.bancha.BanchaPage;
import edu.columbia.ldpd.text.Configuration;
import edu.columbia.ldpd.text.fields.IndexTypes.Multiple;
import edu.columbia.ldpd.text.fields.IndexTypes.Store;
import edu.columbia.ldpd.text.fields.IndexTypes.Tokenize;
import edu.columbia.ldpd.text.impl.BasePageTransformer;


public class SolrInputDocTransformer extends BasePageTransformer<BanchaPage, SolrInputDocument> {
    private final Configuration config;
    private final Hashtable<String,String> idToCollectionHash;
    private final String imprintField;
    private final String urlLabelField;
    public SolrInputDocTransformer(
            Configuration config,
            Hashtable<String,String> idToCollectionHash) {
        this.config = config;
        this.idToCollectionHash = idToCollectionHash;
        this.imprintField = fieldName("imprint",Store.YES,Multiple.NO,Tokenize.NO);
        this.urlLabelField = fieldName("url_label",Store.YES,Multiple.NO,Tokenize.NO);
    }

    @Override
    public SolrInputDocument transform(BanchaPage page) throws BanchaException {
        //System.out.println("Just entered addBookPage()");

        // All the data for the page to be indexed is in the BanchaPage object.
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField(fileNameField, page.getTargetFileName());
        doc.addField(baseNameField, page.getBaseName());
        doc.addField(titleField, page.getTitle());
        doc.addField(authorField, page.getAuthor());
        doc.addField(pageIdField, page.getPageId());
        doc.addField(ID_FIELD, idFor(page));
        doc.addField(pageNumField,page.getPageNum());
        doc.addField(textField, page.getText());

        doc.addField(urlField, page.getUrl(config));

        // These fields do not have to be stored in order to sort by them,
        // but for debugging purposes we'll want to have access to it.
        String sortAuthor = sortable(page.getAuthor());
        doc.addField(sortAuthorField, sortAuthor);

        String sortTitle = sortable(page.getTitleSort());
        doc.addField(sortTitleField, sortTitle);

        doc.addField(docIdField, docIdFor(page));

        String basename = page.getBaseName();
        String collection = (String)idToCollectionHash.get( basename );

        // This gets printed once per PAGE, not once per book, so it's very
        // noisy.  Leave it turned off.
        // if (collection != null) {
        //  System.out.println(basename + " is part of collection " + collection);
        // }
        if (collection != null && !collection.isEmpty()) {
            doc.addField(
                collectionField,
                collection.split("\\s"));
        }

        // debug...
        //System.out.println("id=" + id);
        //System.out.println("collection=" + collection);

        // For simpler searching, concatenate all fields together
        // (Not actually ALL fields, only those a person might search.)
        String[] allFields = {page.getText(),
                           page.getTitle(),
                           page.getAuthor(),
                           page.getImprint()};
        doc.addField(ALL_TEXT_FIELD, allFields);

        // locally defined fields
        doc.addField(imprintField, page.getImprint());
        doc.addField(urlLabelField, page.getUrlLabel());
        return doc;


    }

}
