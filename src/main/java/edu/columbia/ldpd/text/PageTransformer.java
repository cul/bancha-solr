package edu.columbia.ldpd.text;

public interface PageTransformer<Y extends TextPage, T> {
	    public T transform(Y page) throws IndexingException;

	    public String idFor(Y page);
}