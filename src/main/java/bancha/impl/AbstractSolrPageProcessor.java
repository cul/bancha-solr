package bancha.impl;

import java.util.Hashtable;

import org.apache.solr.common.SolrInputDocument;

import bancha.BanchaException;
import bancha.BanchaPage;
import bancha.Configuration;
import bancha.fields.IndexTypes;

public abstract class AbstractSolrPageProcessor extends BasePageProcessor
implements IndexTypes {

    protected final Hashtable<String,String> idToCollectionHash;
    protected final Configuration config;
    protected final String solrUrl;
    private static final byte STORE = 0b0010;
    private static final byte MULTIPLE = 0b0001;
    private static final byte TOKENIZE = 0b0100;
    private static final byte VECTOR = 0b1000;
    private static final String[] SUFFIXES = {
        "_si",
        "_sim",
        "_ssi",
        "_ssim",
        "_tei",
        "_teim",
        "_tesi",
        "_tesim",
        "_si",
        "_sim",
        "_ssi",
        "_ssim",
        "_teiv",
        "_teimv",
        "_tesiv",
        "_tesimv",
    };
    public AbstractSolrPageProcessor(Configuration config,
            Hashtable<String,String> idToCollectionHash) {
        this.config = config;
        this.idToCollectionHash = idToCollectionHash;
        this.solrUrl = config.get("solrUrl");
    }

    protected static String suffix(Store store, Multiple multivalue, Tokenize tokenize, Vector vector) {
        int ix = (((tokenize == Tokenize.YES) ? TOKENIZE : 0B0) |
                ((multivalue == Multiple.YES) ? MULTIPLE : 0B0) |
                ((store == Store.YES) ? STORE : 0B0) |
                ((vector == Vector.YES) ? VECTOR : 0B0));
                
        return SUFFIXES[ix];
    }
    protected String fieldName(String base, Store store, Multiple multivalue, Tokenize tokenize) {
        return fieldName(base,store,multivalue,tokenize,Vector.NO);
        
    }
    protected String fieldName(String base, Store store, Multiple multivalue, Tokenize tokenize, Vector vector) {
        return base + suffix(store, multivalue, tokenize, vector);
    }
    public SolrInputDocument toDocument(BanchaPage page) throws BanchaException {
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
