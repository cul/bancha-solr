package edu.columbia.ldpd.text.nny.impl;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.common.SolrInputDocument;

import edu.columbia.ldpd.text.nny.NNYRecord;
import edu.columbia.ldpd.text.Configuration;
import edu.columbia.ldpd.text.impl.AbstractSolrPageProcessor;
import edu.columbia.ldpd.text.impl.BasePageTransformer;

public class SolrPageProcessor extends AbstractSolrPageProcessor<NNYRecord> {

    private static SolrInputDocTransformer transformer(Configuration config) {
        return new SolrInputDocTransformer(config);
    }
    private static SolrClient client(Configuration config) throws IOException, URISyntaxException {
    	String solrURL = config.get("solrUrl");
    	if (solrURL.startsWith("file")) {
    		return getJsonClient(solrURL);
    	} else {
    		return getHttpClient(solrURL);
    	}
    }

    public SolrPageProcessor(Configuration config) throws IOException, URISyntaxException {
        this(config, client(config), transformer(config));
    }

    public SolrPageProcessor(Configuration config, SolrClient solr) {
        this(config, solr, transformer(config));
    }

    public SolrPageProcessor(Configuration config, SolrClient solr, BasePageTransformer<NNYRecord, SolrInputDocument> transformer) {
        super(config, solr, transformer);
    }
}