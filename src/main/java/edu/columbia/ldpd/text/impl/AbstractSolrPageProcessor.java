package edu.columbia.ldpd.text.impl;

import org.apache.solr.common.SolrInputDocument;

import edu.columbia.ldpd.text.IndexingException;
import edu.columbia.ldpd.text.PageTransformer;
import edu.columbia.ldpd.text.TextPage;
import edu.columbia.ldpd.text.fields.BaseFields;
import edu.columbia.ldpd.text.fields.IndexTypes;
import edu.columbia.ldpd.text.impl.BasePageTransformer;

public abstract class AbstractSolrPageProcessor<T extends TextPage> extends BasePageProcessor<T, SolrInputDocument>
implements IndexTypes, BaseFields<T> {

    protected final BasePageTransformer<T, SolrInputDocument> transformer;
    public AbstractSolrPageProcessor(BasePageTransformer<T, SolrInputDocument> transformer) {
        this.transformer = transformer;
    }

    public SolrInputDocument toDocument(T page) throws IndexingException {
        return transformer.transform(page);
    }

    @Override
    public PageTransformer<T, SolrInputDocument> getTransformer() {
        return transformer;
    }

    @Override
    public String sortable(String src) {
        return transformer.sortable(src);
    }
    @Override
    public String docIdFor(T page) {
        return transformer.docIdFor(page);
    }
    @Override
    public String idFor(T page) {
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
