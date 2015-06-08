package edu.columbia.ldpd.text.impl;

import edu.columbia.ldpd.text.FieldSuffixTransformer;
import edu.columbia.ldpd.text.TextPage;
import edu.columbia.ldpd.text.fields.BaseFields;
import edu.columbia.ldpd.text.fields.IndexTypes.Multiple;
import edu.columbia.ldpd.text.fields.IndexTypes.Store;
import edu.columbia.ldpd.text.fields.IndexTypes.Tokenize;
import edu.columbia.ldpd.text.fields.IndexTypes.Vector;


public abstract class BasePageTransformer<Y extends TextPage, T> extends FieldSuffixTransformer<Y, T>
implements BaseFields<Y> {

    public String fieldName(String base, Store store, Multiple multivalue, Tokenize tokenize) {
        return fieldName(base,store,multivalue,tokenize,Vector.NO);
        
    }
    public String fieldName(String base, Store store, Multiple multivalue, Tokenize tokenize, Vector vector) {
        return base + suffix(store, multivalue, tokenize, vector);
    }
    public String sortable(String src) {
        return src.replaceAll(" ", "_").toLowerCase();
    }
    @Override
    public String docIdFor(Y page) {
        String id = page.getTargetFileName();
        id = id.replaceAll("/pages/.*", "");
        id = id.replaceAll("ldpd_", "");
        return id;
    }
    @Override
    public String idFor(Y page) {
        return docIdFor(page) + "_" + page.getPageId();
    }
}
