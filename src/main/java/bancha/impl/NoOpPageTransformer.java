package bancha.impl;

import bancha.BanchaException;
import bancha.BanchaPage;


public class NoOpPageTransformer extends BasePageTransformer<BanchaPage> {

    @Override
    public BanchaPage transform(BanchaPage page) throws BanchaException {
        return page;
    }

}
