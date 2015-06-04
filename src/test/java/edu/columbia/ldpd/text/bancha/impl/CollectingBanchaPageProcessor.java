package edu.columbia.ldpd.text.bancha.impl;

import edu.columbia.ldpd.text.PageTransformer;
import edu.columbia.ldpd.text.fields.IndexTypes.Multiple;
import edu.columbia.ldpd.text.fields.IndexTypes.Store;
import edu.columbia.ldpd.text.fields.IndexTypes.Tokenize;
import edu.columbia.ldpd.text.fields.IndexTypes.Vector;
import edu.columbia.ldpd.text.impl.NoOpPageTransformer;
import edu.columbia.ldpd.text.bancha.BanchaException;
import edu.columbia.ldpd.text.bancha.BanchaPage;

public class CollectingBanchaPageProcessor extends CollectingPageProcessor<BanchaPage,BanchaPage> {

    private final NoOpPageTransformer<BanchaPage> transformer;
    public CollectingBanchaPageProcessor(){
        super();
        this.transformer = new NoOpPageTransformer<BanchaPage>();
    }
    @Override
    public void processPage(BanchaPage page) throws BanchaException {
        pages.add(page);
    }

    @Override
    public PageTransformer<BanchaPage,BanchaPage> getTransformer() {
        return transformer;
    }

    @Override
    public String sortable(String src) {
        return transformer.sortable(src);
    }

	@Override
	public String docIdFor(BanchaPage page) {
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
