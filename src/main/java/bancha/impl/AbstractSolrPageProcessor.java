package bancha.impl;

import java.util.Hashtable;

import org.apache.solr.common.SolrInputDocument;

import bancha.BanchaException;
import bancha.BanchaPage;
import bancha.BanchaPageTransformer;
import bancha.Configuration;
import bancha.fields.BaseFields;
import bancha.fields.IndexTypes;

public abstract class AbstractSolrPageProcessor extends BasePageProcessor<SolrInputDocument>
implements IndexTypes, BaseFields {

    protected final Configuration config;
    protected final String solrUrl;
    protected final SolrInputDocTransformer transformer;
    public AbstractSolrPageProcessor(Configuration config,
            Hashtable<String,String> idToCollectionHash) {
        this.config = config;
        this.solrUrl = config.get("solrUrl");
        transformer = new SolrInputDocTransformer(config, idToCollectionHash);
    }

    public SolrInputDocument toDocument(BanchaPage page) throws BanchaException {
        return transformer.transform(page);
    }

    @Override
    public BanchaPageTransformer<SolrInputDocument> getTransformer() {
        return transformer;
    }

    @Override
    public String sortable(String src) {
        return transformer.sortable(src);
    }
    @Override
    public String docIdFor(BanchaPage page) {
        return transformer.docIdFor(page);
    }
    @Override
    public String idFor(BanchaPage page) {
        return transformer.idFor(page);
    }
    @Override
    public String fieldName(String base, Store store, Multiple multivalue, Tokenize tokenize) {
        return transformer.fieldName(base, store, multivalue, tokenize);
    }
    @Override
    public String fieldName(String base, Store store, Multiple multivalue, Tokenize tokenize, Vector vector){
        return transformer.fieldName(base, store, multivalue, tokenize,vector);
    }
}
