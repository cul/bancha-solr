package edu.columbia.ldpd.text;

import java.io.IOException;

import javax.xml.parsers.SAXParser;

import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

import edu.columbia.ldpd.text.IndexingException;

public interface TextIndexer {
	void indexXml (String xmlfile) throws IOException, SAXException;
	Digester getDigester();
	void run(Configuration config, SAXParser parser) throws IndexingException;
	void cleanUp() throws IndexingException;
}
