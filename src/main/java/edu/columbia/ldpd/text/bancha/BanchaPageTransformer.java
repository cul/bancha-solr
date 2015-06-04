package edu.columbia.ldpd.text.bancha;

import edu.columbia.ldpd.text.PageTransformer;


public interface BanchaPageTransformer<T> extends PageTransformer<BanchaPage, T>{
    public T transform(BanchaPage page) throws BanchaException;

    public String idFor(BanchaPage page);
}