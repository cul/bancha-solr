package edu.columbia.ldpd.text.impl;

import java.io.IOException;

import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

import edu.columbia.ldpd.text.IndexingException;
import edu.columbia.ldpd.text.TextIndexer;

public abstract class AbstractTextIndexer implements TextIndexer {

    private final Digester digester;

    public AbstractTextIndexer(Digester digester) {
        this.digester = digester;
    }

    @Override
	public void indexXml(String xmlUri) throws IOException, SAXException {
        System.out.println("Indexing " + xmlUri);

        // Parse the TEI XML doc using Digester (SAX wrapper)
        getDigester().parse(xmlUri);
	}

	@Override
	public Digester getDigester() {
		return this.digester;
	}

	@Override
	public void cleanUp() throws IndexingException {
		
	}
}
