package edu.columbia.ldpd.text.impl;

import edu.columbia.ldpd.text.PageTransformer;
import edu.columbia.ldpd.text.TextPage;
import edu.columbia.ldpd.text.fields.IndexTypes.Multiple;
import edu.columbia.ldpd.text.fields.IndexTypes.Store;
import edu.columbia.ldpd.text.fields.IndexTypes.Tokenize;
import edu.columbia.ldpd.text.fields.IndexTypes.Vector;
import edu.columbia.ldpd.text.impl.NoOpPageTransformer;

public class CollectingPageProcessor<T extends TextPage> extends edu.columbia.ldpd.text.CollectingPageProcessor<T,T> {

    private final NoOpPageTransformer<T> transformer;
    public CollectingPageProcessor(){
        super();
        this.transformer = new NoOpPageTransformer<>();
    }
    @Override
    public void processPage(T page) {
        pages.add(page);
    }

    @Override
    public PageTransformer<T,T> getTransformer() {
        return transformer;
    }

    @Override
    public String sortable(String src) {
        return transformer.sortable(src);
    }

	@Override
	public String docIdFor(T page) {
		return this.transformer.docIdFor(page);
	}

	@Override
	public String fieldName(String base, Store store, Multiple multivalue,
			Tokenize tokenize) {
		return this.transformer.fieldName(base, store, multivalue, tokenize);
	}

	@Override
	public String fieldName(String base, Store store, Multiple multivalue,
			Tokenize tokenize, Vector vector) {
		return this.transformer.fieldName(base, store, multivalue, tokenize, vector);
	}

}
