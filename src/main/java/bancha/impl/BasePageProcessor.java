package bancha.impl;

import bancha.BanchaPage;
import bancha.PageProcessor;


public abstract class BasePageProcessor implements PageProcessor {
    protected String sortable(String src) {
        return src.replaceAll(" ", "_").toLowerCase();
    }
    protected String idFor(BanchaPage page) {
        String id = page.getTargetFileName();
        id = id.replaceAll("_[0-9][0-9][0-9]/pages/.*", "");
        id = id.replaceAll("ldpd_", "");
        return id;
    }
}
