package edu.columbia.ldpd.text.bancha;

import edu.columbia.ldpd.text.IndexingException;


@SuppressWarnings("serial")
public class BanchaException extends IndexingException {

    public BanchaException(String message, Exception e) {
        super(message, e);
    }

}