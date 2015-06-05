package edu.columbia.ldpd.text.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;

import edu.columbia.ldpd.text.Configuration;
import edu.columbia.ldpd.text.IndexingException;
import edu.columbia.ldpd.text.PageTransformer;
import edu.columbia.ldpd.text.TextPage;
import edu.columbia.ldpd.text.bancha.BanchaException;
import edu.columbia.ldpd.text.fields.BaseFields;
import edu.columbia.ldpd.text.fields.IndexTypes;
import edu.columbia.ldpd.text.impl.BasePageTransformer;

public abstract class AbstractSolrPageProcessor<T extends TextPage> extends BasePageProcessor<T, SolrInputDocument>
implements IndexTypes, BaseFields<T> {

	public static final int DEFAULT_BATCH_SIZE = 10;

    protected final BasePageTransformer<T, SolrInputDocument> transformer;
	protected int batchSize = AbstractSolrPageProcessor.DEFAULT_BATCH_SIZE;

	protected final Configuration config;
    protected SolrServer solr;
    protected final List<SolrInputDocument> solrDocs;

    public AbstractSolrPageProcessor(Configuration config, SolrServer solr, BasePageTransformer<T, SolrInputDocument> transformer) {
        this.transformer = transformer;
        this.config = config;
        this.solr = solr;
        this.solrDocs = new ArrayList<>();
    }

    public SolrInputDocument toDocument(T page) throws IndexingException {
        return transformer.transform(page);
    }

    public int batchSize() {
    	return this.batchSize;
    }

    public int batchSize(int batchSize) {
    	if (batchSize < 1) throw new IllegalArgumentException("Bad batchSize value \"" + batchSize + "\"");
    	this.batchSize = batchSize;
    	return this.batchSize;
    }

    @Override
    public void processPage(T page) throws IndexingException {
        SolrInputDocument doc = toDocument(page);
    	if (this.config.onlyCollections() && "".equals(doc.getFieldValue(transformer.fieldName("collection",Store.YES,Multiple.NO,Tokenize.NO)))) return;
        solrDocs.add(doc);
        if (solrDocs.size() > batchSize) {
            try {
                post();
            } catch (Exception e) {
                throw new BanchaException(e.getMessage(),e);
            }
        }
    }

    protected void post() throws SolrServerException, IOException{
        if (solrDocs.size() == 0) return;
        
        solr.add(solrDocs);
        solr.commit();
        solrDocs.clear();
    }


    @Override
    public void cleanUp() throws BanchaException {
        try {
            post();
            if (solr != null) solr.optimize();
        } catch (Exception e) {
            throw new BanchaException(e.getMessage(),e);
        }
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
