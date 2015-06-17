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
    public static String ID_FIELD = "id";
    /** a field name following the Hydra pattern for stored, indexed, vectored,
        multivalued text
    */
    public static String ALL_TEXT_FIELD = "all_text_tsimv";

    protected final String fileNameField;
    protected final String baseNameField;
    protected final String titleField;
    protected final String authorField;
    protected final String pageIdField;
    protected final String pageNumField;
    protected final String pageNumSortField = "page_num_isi";
    protected final String textField;
    protected final String urlField;
    protected final String sortAuthorField;
    protected final String sortTitleField;
    protected final String docIdField;
    protected final String collectionField;

    public BasePageTransformer() {
        this.fileNameField = fieldName("target_filename",Store.YES,Multiple.NO,Tokenize.NO);
        this.baseNameField = fieldName("basename",Store.YES,Multiple.NO,Tokenize.NO);
        this.titleField = fieldName("title",Store.YES,Multiple.NO,Tokenize.NO);
        this.authorField = fieldName("author",Store.YES,Multiple.NO,Tokenize.NO);
        this.pageIdField = fieldName("page_id",Store.YES,Multiple.NO,Tokenize.NO);
        this.pageNumField = fieldName("page_num",Store.YES,Multiple.NO,Tokenize.NO);
        this.textField = fieldName("text",Store.YES,Multiple.NO,Tokenize.YES);
        this.urlField = fieldName("url",Store.YES,Multiple.NO,Tokenize.NO);
        this.sortAuthorField = fieldName("sort_author",Store.YES,Multiple.NO,Tokenize.NO);
        this.sortTitleField = fieldName("sort_title",Store.YES,Multiple.NO,Tokenize.NO);
        this.docIdField = fieldName("doc_id",Store.YES,Multiple.NO,Tokenize.NO);
        this.collectionField = fieldName("collection",Store.YES,Multiple.YES,Tokenize.NO);
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
