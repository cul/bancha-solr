package edu.columbia.ldpd.text.impl;

import edu.columbia.ldpd.text.IndexingException;
import edu.columbia.ldpd.text.TextPage;
import edu.columbia.ldpd.text.impl.BasePageTransformer;


public class NoOpPageTransformer<T extends TextPage> extends BasePageTransformer<T,T> {

    @Override
    public T transform(T page) throws IndexingException {
        return page;
    }

}
