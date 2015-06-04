package edu.columbia.ldpd.text.nny.impl;

import edu.columbia.ldpd.text.IndexingException;

@SuppressWarnings("serial")
public class NNYException extends IndexingException {

	public NNYException(String message, Exception e) {
		super(message, e);
	}

}
