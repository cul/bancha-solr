package bancha.impl;

import bancha.BanchaPage;
import bancha.BanchaPageTransformer;
import bancha.fields.BaseFields;
import bancha.fields.IndexTypes.Multiple;
import bancha.fields.IndexTypes.Store;
import bancha.fields.IndexTypes.Tokenize;
import bancha.fields.IndexTypes.Vector;


public abstract class BasePageTransformer<T> implements BanchaPageTransformer<T>, BaseFields {
    private static final byte STORE = 0b0010;
    private static final byte MULTIPLE = 0b0001;
    private static final byte TOKENIZE = 0b0100;
    private static final byte VECTOR = 0b1000;
    private static final String[] SUFFIXES = {
        "_si",
        "_sim",
        "_ssi",
        "_ssim",
        "_tei",
        "_teim",
        "_tesi",
        "_tesim",
        "_si",
        "_sim",
        "_ssi",
        "_ssim",
        "_teiv",
        "_teimv",
        "_tesiv",
        "_tesimv",
    };
    protected static String suffix(Store store, Multiple multivalue, Tokenize tokenize, Vector vector) {
        int ix = (((tokenize == Tokenize.YES) ? TOKENIZE : 0B0) |
                ((multivalue == Multiple.YES) ? MULTIPLE : 0B0) |
                ((store == Store.YES) ? STORE : 0B0) |
                ((vector == Vector.YES) ? VECTOR : 0B0));
                
        return SUFFIXES[ix];
    }
    public String fieldName(String base, Store store, Multiple multivalue, Tokenize tokenize) {
        return fieldName(base,store,multivalue,tokenize,Vector.NO);
        
    }
    public String fieldName(String base, Store store, Multiple multivalue, Tokenize tokenize, Vector vector) {
        return base + suffix(store, multivalue, tokenize, vector);
    }
    public String sortable(String src) {
        return src.replaceAll(" ", "_").toLowerCase();
    }
    public String docIdFor(BanchaPage page) {
        String id = page.getTargetFileName();
        id = id.replaceAll("_\\d{3}/pages/.*", "");
        id = id.replaceAll("ldpd_", "");
        return id;
    }
    public String idFor(BanchaPage page) {
        return docIdFor(page) + "_" + page.getPageId();
    }
}
