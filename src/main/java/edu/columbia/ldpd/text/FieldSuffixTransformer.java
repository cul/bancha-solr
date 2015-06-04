package edu.columbia.ldpd.text;

import edu.columbia.ldpd.text.fields.IndexTypes.Multiple;
import edu.columbia.ldpd.text.fields.IndexTypes.Store;
import edu.columbia.ldpd.text.fields.IndexTypes.Tokenize;
import edu.columbia.ldpd.text.fields.IndexTypes.Vector;

public abstract class FieldSuffixTransformer<Y extends TextPage, T> implements PageTransformer<Y, T> {
    private static final byte STORE = 0b0010;
    private static final byte MULTIPLE = 0b0001;
    private static final byte TOKENIZE = 0b0100;
    private static final byte VECTOR = 0b1000;
    private static final String[] SUFFIXES = {
        "_si",
        "_sim",
        "_ssi",
        "_ssim",
        "_ti",
        "_tim",
        "_tsi",
        "_tsim",
        "_si",
        "_sim",
        "_ssi",
        "_ssim",
        "_tiv",
        "_timv",
        "_tsiv",
        "_tsimv",
    };
    public String suffix(Store store, Multiple multivalue, Tokenize tokenize, Vector vector) {
        int ix = (((tokenize == Tokenize.YES) ? TOKENIZE : 0B0) |
                ((multivalue == Multiple.YES) ? MULTIPLE : 0B0) |
                ((store == Store.YES) ? STORE : 0B0) |
                ((vector == Vector.YES) ? VECTOR : 0B0));
                
        return SUFFIXES[ix];
    }

}
