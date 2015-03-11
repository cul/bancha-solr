package bancha.impl;

import java.util.Hashtable;

import org.apache.solr.common.SolrInputDocument;

import bancha.BanchaException;
import bancha.BanchaPage;
import bancha.Configuration;
import bancha.fields.IndexTypes.Multiple;
import bancha.fields.IndexTypes.Store;
import bancha.fields.IndexTypes.Tokenize;


public class SolrInputDocTransformer extends BasePageTransformer<SolrInputDocument> {
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
        doc.addField(fieldName("pageNum",Store.YES,Multiple.NO,Tokenize.NO), page.getPageNum());
        doc.addField(fieldName("imprint",Store.YES,Multiple.NO,Tokenize.NO), page.getImprint());
        doc.addField(fieldName("url_label",Store.YES,Multiple.NO,Tokenize.NO), page.getUrlLabel());
        doc.addField(fieldName("text",Store.YES,Multiple.NO,Tokenize.YES), page.getText());

        // Synthetic fields in the lucene index

        doc.addField(fieldName("url",Store.YES,Multiple.NO,Tokenize.NO), config.urlPrefix() + "/" + page.getTargetFileName());

        // These fields do not have to be stored in order to sort by them,
        // but for debugging purposes we'll want to have access to it.
        String sortAuthor = sortable(page.getAuthor());
        doc.addField(fieldName("sortAuthor",Store.YES,Multiple.NO,Tokenize.NO), sortAuthor);

        String sortTitle = sortable(page.getTitleSort());
        doc.addField(fieldName("sortTitle",Store.YES,Multiple.NO,Tokenize.NO), sortTitle);

        doc.addField(fieldName("doc_id",Store.YES,Multiple.NO,Tokenize.NO), docIdFor(page));

        String basename = page.getBaseName();
        String collection = (String)idToCollectionHash.get( basename );
        if (collection == null) collection = "";

        // This gets printed once per PAGE, not once per book, so it's very
        // noisy.  Leave it turned off.
        // if (collection != null) {
        //  System.out.println(basename + " is part of collection " + collection);
        // }
        if (!collection.isEmpty()) {
            doc.addField(
                fieldName("collection",Store.YES,Multiple.NO,Tokenize.NO),
                collection);
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
