package edu.columbia.ldpd.text.impl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.Http2SolrClient;
import org.apache.solr.common.SolrInputDocument;

import edu.columbia.ldpd.text.Configuration;
import edu.columbia.ldpd.text.JsonLoggingSolrClient;
import edu.columbia.ldpd.text.IndexingException;
import edu.columbia.ldpd.text.PageTransformer;
import edu.columbia.ldpd.text.TextPage;
import edu.columbia.ldpd.text.bancha.BanchaException;
import edu.columbia.ldpd.text.fields.BaseFields;
import edu.columbia.ldpd.text.fields.IndexTypes;

public abstract class AbstractSolrPageProcessor<T extends TextPage> extends BasePageProcessor<T, SolrInputDocument>
implements IndexTypes, BaseFields<T> {

    public static final int DEFAULT_BATCH_SIZE = 10;

    protected final BasePageTransformer<T, SolrInputDocument> transformer;
    protected int batchSize = AbstractSolrPageProcessor.DEFAULT_BATCH_SIZE;

    protected final Configuration config;
    protected SolrClient solr;
    protected final List<SolrInputDocument> solrDocs;

    protected static SolrClient getHttpClient(String solrURL) {
        return new Http2SolrClient.Builder(solrURL)
        .connectionTimeout(10000)
        .idleTimeout(60000)
        .build();
    }

    protected static SolrClient getJsonClient(String solrURL) throws IOException, URISyntaxException {
        return new JsonLoggingSolrClient(solrURL);
    }

    public AbstractSolrPageProcessor(Configuration config, SolrClient solr, BasePageTransformer<T, SolrInputDocument> transformer) {
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
        if (this.config.onlyCollections()) {
            Object c = doc.getFieldValue(transformer.fieldName("collection",Store.YES,Multiple.YES,Tokenize.NO));
            if (c == null || "".equals(c.toString())) return;
        }
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
