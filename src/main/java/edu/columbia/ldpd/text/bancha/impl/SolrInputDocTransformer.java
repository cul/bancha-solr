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
    public SolrInputDocTransformer(
            Configuration config,
            Hashtable<String,String> idToCollectionHash) {
        this.config = config;
        this.idToCollectionHash = idToCollectionHash;
    }

    @Override
    public SolrInputDocument transform(BanchaPage page) throws BanchaException {
        //System.out.println("Just entered addBookPage()");

        // All the data for the page to be indexed is in the BanchaPage object.
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField(fieldName("target_filename",Store.YES,Multiple.NO,Tokenize.NO),
                page.getTargetFileName());
        //System.out.println( page.getTargetFileName() );
        doc.addField(fieldName("basename",Store.YES,Multiple.NO,Tokenize.NO), page.getBaseName());
        doc.addField(fieldName("title",Store.YES,Multiple.NO,Tokenize.NO), page.getTitle());
        doc.addField(fieldName("author",Store.YES,Multiple.NO,Tokenize.NO), page.getAuthor());
        doc.addField(fieldName("page_id",Store.YES,Multiple.NO,Tokenize.NO), page.getPageId());
        doc.addField("id", idFor(page));
        doc.addField(fieldName("page_num",Store.YES,Multiple.NO,Tokenize.NO), page.getPageNum());
        doc.addField(fieldName("imprint",Store.YES,Multiple.NO,Tokenize.NO), page.getImprint());
        doc.addField(fieldName("url_label",Store.YES,Multiple.NO,Tokenize.NO), page.getUrlLabel());
        doc.addField(fieldName("text",Store.YES,Multiple.NO,Tokenize.YES), page.getText());

        doc.addField(fieldName("url",Store.YES,Multiple.NO,Tokenize.NO), page.getUrl(config));

        // These fields do not have to be stored in order to sort by them,
        // but for debugging purposes we'll want to have access to it.
        String sortAuthor = sortable(page.getAuthor());
        doc.addField(fieldName("sort_author",Store.YES,Multiple.NO,Tokenize.NO), sortAuthor);

        String sortTitle = sortable(page.getTitleSort());
        doc.addField(fieldName("sort_title",Store.YES,Multiple.NO,Tokenize.NO), sortTitle);

        doc.addField(fieldName("doc_id",Store.YES,Multiple.NO,Tokenize.NO), docIdFor(page));

        String basename = page.getBaseName();
        String collection = (String)idToCollectionHash.get( basename );

        // This gets printed once per PAGE, not once per book, so it's very
        // noisy.  Leave it turned off.
        // if (collection != null) {
        //  System.out.println(basename + " is part of collection " + collection);
        // }
        if (collection != null && !collection.isEmpty()) {
            doc.addField(
                fieldName("collection",Store.YES,Multiple.YES,Tokenize.NO),
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
        doc.addField("all_text_timv", allFields);
        return doc;


    }

}
