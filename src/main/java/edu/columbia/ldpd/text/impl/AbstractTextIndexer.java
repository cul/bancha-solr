package edu.columbia.ldpd.text.impl;

import org.apache.commons.digester.Digester;

import edu.columbia.ldpd.text.IndexingException;
import edu.columbia.ldpd.text.TextIndexer;

public abstract class AbstractTextIndexer implements TextIndexer {

    private final Digester digester;

    public AbstractTextIndexer(Digester digester) {
        this.digester = digester;
    }

    @Override
	public void indexXml(String xmlUri) {
        System.out.println("Indexing " + xmlUri);

        // Parse the TEI XML doc using Digester (SAX wrapper)
        try {
            getDigester().parse(xmlUri);
        } catch (Exception e) {
            System.err.println("Exception in digester.parse(" + xmlUri + ")" );
            System.err.println(e.getClass() + ": " + e.getMessage() );
        }
	}

	@Override
	public Digester getDigester() {
		return this.digester;
	}

	@Override
	public void cleanUp() throws IndexingException {
		
	}
}
