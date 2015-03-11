package bancha.fields;

import bancha.BanchaPage;
import bancha.fields.IndexTypes.Multiple;
import bancha.fields.IndexTypes.Store;
import bancha.fields.IndexTypes.Tokenize;
import bancha.fields.IndexTypes.Vector;


public interface BaseFields {
    public String sortable(String src);
    public String docIdFor(BanchaPage page);
    public String idFor(BanchaPage page);
    public String fieldName(String base, Store store, Multiple multivalue, Tokenize tokenize);
    public String fieldName(String base, Store store, Multiple multivalue, Tokenize tokenize, Vector vector);
}
