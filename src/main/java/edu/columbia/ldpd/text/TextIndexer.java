package edu.columbia.ldpd.text;

import javax.xml.parsers.SAXParser;

import org.apache.commons.digester.Digester;

import edu.columbia.ldpd.text.IndexingException;

public interface TextIndexer {
	void indexXml (String xmlfile);
	Digester getDigester();
	void run(Configuration config, SAXParser parser) throws IndexingException;
	void cleanUp() throws IndexingException;
}
