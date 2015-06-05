package edu.columbia.ldpd.text.bancha.impl;

import java.util.Hashtable;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

import edu.columbia.ldpd.text.Configuration;
import edu.columbia.ldpd.text.impl.AbstractSolrPageProcessor;
import edu.columbia.ldpd.text.impl.BasePageTransformer;
import edu.columbia.ldpd.text.bancha.BanchaPage;

public class SolrPageProcessor extends AbstractSolrPageProcessor<BanchaPage> {

    private static SolrInputDocTransformer transformer(Configuration config, Hashtable<String,String> idToCollectionHash) {
    	return new SolrInputDocTransformer(config, idToCollectionHash);
    }

    public SolrPageProcessor(Configuration config, Hashtable<String,String> idToCollectionHash) {
        this(config,new HttpSolrServer(config.get("solrUrl")), transformer(config, idToCollectionHash));
    }

    public SolrPageProcessor(Configuration config, SolrServer solr, Hashtable<String,String> idToCollectionHash) {
        this(config,solr, transformer(config, idToCollectionHash));
    }

    public SolrPageProcessor(Configuration config, SolrServer solr, BasePageTransformer<BanchaPage, SolrInputDocument> transformer) {
        super(config, solr, transformer);
    }

}