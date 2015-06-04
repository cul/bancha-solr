package edu.columbia.ldpd.text;


public interface PageProcessor<T extends TextPage> {
    void processPage(T page) throws IndexingException;
    void cleanUp() throws IndexingException;
}
