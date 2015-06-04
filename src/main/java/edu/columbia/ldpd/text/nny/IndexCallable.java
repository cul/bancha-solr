package edu.columbia.ldpd.text.nny;

import java.util.concurrent.Callable;

import edu.columbia.ldpd.text.Configuration;
import edu.columbia.ldpd.text.PageProcessor;
import edu.columbia.ldpd.text.TextIndexer;
import edu.columbia.ldpd.text.nny.impl.SolrPageProcessor;

public class IndexCallable implements Callable<TextIndexer> {

	private final Configuration config;
	public IndexCallable() {
		this(new Configuration());
	}

	public IndexCallable(Configuration config) {
		this.config = config;
	}

	@Override
	public TextIndexer call() throws Exception {
        PageProcessor<NNYRecord> proc = null;
        proc = new SolrPageProcessor(config);


    	BuildIndex indexer = new BuildIndex(proc);
    	indexer.run(config, null);
    	indexer.cleanUp();

    	return indexer;
	}

}
