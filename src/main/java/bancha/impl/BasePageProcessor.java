package bancha.impl;

import bancha.BanchaPage;
import bancha.BanchaPageTransformer;
import bancha.PageProcessor;


public abstract class BasePageProcessor<T> implements PageProcessor {
    public abstract BanchaPageTransformer<T> getTransformer();

    public String idFor(BanchaPage page) {
        return getTransformer().idFor(page);
    }

}
