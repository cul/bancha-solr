package edu.columbia.ldpd.text.impl;

import edu.columbia.ldpd.text.PageProcessor;
import edu.columbia.ldpd.text.PageTransformer;
import edu.columbia.ldpd.text.TextPage;
import edu.columbia.ldpd.text.fields.BaseFields;


public abstract class BasePageProcessor<Y extends TextPage, T> implements PageProcessor<Y>, BaseFields<Y> {
    public abstract PageTransformer<Y, T> getTransformer();

    public String idFor(Y page) {
        return getTransformer().idFor(page);
    }

}