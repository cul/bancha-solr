package edu.columbia.ldpd.text;

public interface TextPage {
    public void setPageId(String newPageId);
    public String getPageId();
	public void setText(String newText);
    public String getText();
    public void setTargetFileName(String newTargetFileName);
    public String getTargetFileName();
    public String getTitle();
    public String getTitleSort();
    public String getAuthor();
    public String getUrl(Configuration config);
}