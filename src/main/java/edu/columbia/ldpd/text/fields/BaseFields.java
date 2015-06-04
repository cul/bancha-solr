package edu.columbia.ldpd.text.fields;

import edu.columbia.ldpd.text.TextPage;
import edu.columbia.ldpd.text.fields.IndexTypes.Multiple;
import edu.columbia.ldpd.text.fields.IndexTypes.Store;
import edu.columbia.ldpd.text.fields.IndexTypes.Tokenize;
import edu.columbia.ldpd.text.fields.IndexTypes.Vector;


public interface BaseFields<T extends TextPage> {
    public String sortable(String src);
    public String docIdFor(T page);
    public String idFor(T page);
    public String fieldName(String base, Store store, Multiple multivalue, Tokenize tokenize);
    public String fieldName(String base, Store store, Multiple multivalue, Tokenize tokenize, Vector vector);
}
