package edu.columbia.ldpd.text.bancha.impl;

import java.util.Hashtable;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.Http2SolrClient;
import org.apache.solr.common.SolrInputDocument;

import edu.columbia.ldpd.text.Configuration;
import edu.columbia.ldpd.text.impl.AbstractSolrPageProcessor;
import edu.columbia.ldpd.text.impl.BasePageTransformer;
import edu.columbia.ldpd.text.bancha.BanchaPage;

public class SolrPageProcessor extends AbstractSolrPageProcessor<BanchaPage> {

    private static SolrInputDocTransformer transformer(Configuration config, Hashtable<String,String> idToCollectionHash) {
    	return new SolrInputDocTransformer(config, idToCollectionHash);
    }

    protected static SolrClient getHttpClient(String solrURL) {
        return new Http2SolrClient.Builder(solrURL)
        .connectionTimeout(10000)
        .idleTimeout(60000)
        .build();
    }

    public SolrPageProcessor(Configuration config, Hashtable<String,String> idToCollectionHash) {
        this(config, getHttpClient(config.get("solrUrl")), transformer(config, idToCollectionHash));
    }

    public SolrPageProcessor(Configuration config, SolrClient solr, Hashtable<String,String> idToCollectionHash) {
        this(config,solr, transformer(config, idToCollectionHash));
    }

    public SolrPageProcessor(Configuration config, SolrClient solr, BasePageTransformer<BanchaPage, SolrInputDocument> transformer) {
        super(config, solr, transformer);
    }

}