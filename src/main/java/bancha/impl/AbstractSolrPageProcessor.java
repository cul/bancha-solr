package bancha.impl;

import java.util.Hashtable;

import org.apache.solr.common.SolrInputDocument;

import bancha.BanchaException;
import bancha.BanchaPage;
import bancha.Configuration;

public abstract class AbstractSolrPageProcessor extends BasePageProcessor {

    protected final Hashtable<String,String> idToCollectionHash;
    protected final Configuration config;
    protected final String solrUrl;
    private static final byte STORE = 0b100;
    private static final byte MULTIPLE = 0b010;
    private static final byte TOKENIZE = 0b001;
    private static final String[] SUFFIXES = {
        "_si",
        "_tei",
        "_sim",
        "_teim",
        "_ssi",
        "_tesi",
        "_ssim",
        "_tesim",
    };
    public AbstractSolrPageProcessor(Configuration config,
            Hashtable<String,String> idToCollectionHash) {
        this.config = config;
        this.idToCollectionHash = idToCollectionHash;
        this.solrUrl = config.get("solrUrl");
    }

    protected static String suffix(boolean store, boolean multivalue, boolean tokenize) {
        int ix = (((tokenize) ? TOKENIZE : 0B0) |
                ((multivalue) ? MULTIPLE : 0B0) |
                ((store) ? STORE : 0B0));
                
        return SUFFIXES[ix];
    }
    protected String fieldName(String base, boolean store, boolean multivalue, boolean tokenize) {
        return base + suffix(store, multivalue, tokenize);        
    }
    public SolrInputDocument toDocument(BanchaPage page) throws BanchaException {
        //System.out.println("Just entered addBookPage()");

        // All the data for the page to be indexed is in the BanchaPage object.
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField(fieldName("target_filename",true,false,false),
                page.getTargetFileName());
        //System.out.println( page.getTargetFileName() );
        doc.addField(fieldName("basename",true,false,false), page.getBaseName());
        doc.addField(fieldName("title",true,false,false), page.getTitle());
        doc.addField(fieldName("author",true,false,false), page.getAuthor());
        doc.addField(fieldName("page_id",true,false,false), page.getPageId());
        doc.addField("id", idFor(page));
        doc.addField(fieldName("pageNum",true,false,false), page.getPageNum());
        doc.addField(fieldName("imprint",true,false,false), page.getImprint());
        doc.addField(fieldName("url_label",true,false,false), page.getUrlLabel());
        doc.addField(fieldName("text",true,false,true), page.getText());

        // Synthetic fields in the lucene index

        doc.addField(fieldName("url",true,false,true), config.urlPrefix() + "/" + page.getTargetFileName());

        // These fields do not have to be stored in order to sort by them,
        // but for debugging purposes we'll want to have access to it.
        String sortAuthor = sortable(page.getAuthor());
        doc.addField(fieldName("sortAuthor",true,false,false), sortAuthor);

        String sortTitle = sortable(page.getTitleSort());
        doc.addField(fieldName("sortTitle",true,false,false), sortTitle);

        doc.addField(fieldName("doc_id",true,false,false), docIdFor(page));

        String basename = page.getBaseName();
        String collection = (String)idToCollectionHash.get( basename );
        if (collection == null) collection = "";

        // This gets printed once per PAGE, not once per book, so it's very
        // noisy.  Leave it turned off.
        // if (collection != null) {
        //  System.out.println(basename + " is part of collection " + collection);
        // }

        doc.addField(fieldName("collection",true,false,false), collection);

        // debug...
        //System.out.println("id=" + id);
        //System.out.println("collection=" + collection);

        // For simpler searching, concatenate all fields together
        // (Not actually ALL fields, only those a person might search.)
        String allFields = page.getText() + " " +
                           page.getTitle() + " " +
                           page.getAuthor() + " " +
                           page.getImprint();
        doc.addField(fieldName("allFields",true,false,true), allFields);
        return doc;


    }

}
