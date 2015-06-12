package edu.columbia.ldpd.text.bancha;

import edu.columbia.ldpd.text.Configuration;
import edu.columbia.ldpd.text.TextPage;

/**
   * JavaBean class that holds properties of each BanchaPage entry.
   */
  public class BanchaPage implements TextPage
  {
    private String targetFileName = null;
    private String baseName = null;
    private String title = null;
    private String title_sort = null;
    private String author = null;
    private String pageId = null;
    private String pageNum = null;
    private String imprint = null;
    private String url_label = null;
    private String text = null;

    public void setTargetFileName(String newTargetFileName) {
    	targetFileName = newTargetFileName;
    }
    public String getTargetFileName() {
    	return targetFileName;
    }

    public void setBaseName(String newBaseName) {
    	baseName = newBaseName;
    }
    public String getBaseName() {
    	return baseName;
    }

    public void setTitle(String newTitle) {
    	title = newTitle;
    }
    public String getTitle() {
    	return title;
    }

    public void setTitleSort(String newTitleSort) {
    	title_sort = newTitleSort;
    }
    public String getTitleSort() {
    	return title_sort;
    }

    public void setAuthor(String newAuthor) {
    	author = newAuthor;
    }
    public String getAuthor() {
    	return author;
    }

    public void setPageId(String newPageId) {
    	pageId = newPageId;
    }
    public String getPageId() {
    	return pageId;
    }

    public void setPageNum(String newPageNum) {
    	pageNum = newPageNum;
    }
    public String getPageNum() {
    	return pageNum;
    }

    public void setImprint(String newImprint) {
    	imprint = newImprint;
    }
    public String getImprint() {
    	return imprint;
    }

    public void setUrlLabel(String newUrlLabel) {
    	url_label = newUrlLabel;
    }
    public String getUrlLabel() { 
      if (url_label == null) return "";
      return url_label; 
    }

    public void setText(String newText) {
    	text = newText;
    }
    public String getText() {
    	return text;
    }
    public String getUrl(Configuration config) {
        return config.urlPrefix() + "/" + getTargetFileName();
    }
  }