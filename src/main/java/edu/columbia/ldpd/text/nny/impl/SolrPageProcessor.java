package edu.columbia.ldpd.text.nny.impl;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

import edu.columbia.ldpd.text.nny.NNYRecord;
import edu.columbia.ldpd.text.Configuration;
import edu.columbia.ldpd.text.impl.AbstractSolrPageProcessor;
import edu.columbia.ldpd.text.impl.BasePageTransformer;

public class SolrPageProcessor extends AbstractSolrPageProcessor<NNYRecord> {

    private static SolrInputDocTransformer transformer(Configuration config) {
    	return new SolrInputDocTransformer(config);
    }

    public SolrPageProcessor(Configuration config) {
        this(config, new HttpSolrServer(config.get("solrUrl")), transformer(config));
    }

    public SolrPageProcessor(Configuration config, SolrServer solr) {
        this(config, solr, transformer(config));
    }

    public SolrPageProcessor(Configuration config, SolrServer solr, BasePageTransformer<NNYRecord, SolrInputDocument> transformer) {
        super(config, solr, transformer);
    }
}