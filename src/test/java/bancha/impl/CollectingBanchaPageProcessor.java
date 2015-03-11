package bancha.impl;

import bancha.BanchaException;
import bancha.BanchaPage;
import bancha.BanchaPageTransformer;


public class CollectingBanchaPageProcessor extends CollectingPageProcessor<BanchaPage> {

    private final NoOpPageTransformer transformer;
    public CollectingBanchaPageProcessor(){
        super();
        this.transformer = new NoOpPageTransformer();
    }
    @Override
    public void processPage(BanchaPage page) throws BanchaException {
        pages.add(page);
    }

    @Override
    public BanchaPageTransformer<BanchaPage> getTransformer() {
        return transformer;
    }

}
