package edu.columbia.ldpd.text.nny;

import edu.columbia.ldpd.text.Configuration;
import edu.columbia.ldpd.text.TextPage;

/**
 * JavaBean class that holds properties of each NNYRecord entry.
 */
public class NNYRecord implements TextPage {

    private String baseName = null;
    private String interviewer = null;
    private String interviewee = null;
    private String interviewNum = null;
    private String sessionNum = null;
    private String pageId = null;
    private String pageNum = null;
    private String targetFileName = null;
    private String text = null;
    private String year = null;

    public void setBaseName(String newBaseName) {
        baseName = newBaseName;
    }

    public String getBaseName() {
        return baseName;
    }

    public void setInterviewee(String newInterviewee) {
        interviewee = newInterviewee;
        }

    public String getInterviewee() {
        return interviewee;
    }

    public String getAuthor() {
    	return getInterviewee();
    }
    public void setInterviewer(String newInterviewer) {
        interviewer = newInterviewer;
    }

    public String getInterviewer() {
        return interviewer;
    }

    public void setInterviewNum(String newInterviewNum) {
        interviewNum = newInterviewNum;
    }

    public String getInterviewNum()
    {
        return interviewNum;
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

    public void setSessionNum(String newSessionNum) {
        sessionNum = newSessionNum;
    }

    public String getSessionNum() {
        return sessionNum;
    }

    public void setTargetFileName(String newTargetFileName) {
        targetFileName = newTargetFileName;
    }

    public String getTargetFileName() {
        return targetFileName;
    }

    public void setText(String newText) {
        text = newText;
    }

    public String getText() {
        return text;
    }

    public void setYear(String newYear) {
        year = newYear;
    }

    public String getYear() {
        return year;
    }
    public String getTitle() {
    	return new StringBuffer().append(interviewee).append(" (").append(year).append(") - Interview ").
    			append(interviewNum).append(", Session ").append(sessionNum).append(", Page ").append(pageNum)
    			.toString();
    }
    
    public String getTitleSort() {
    	return getTitle();
    }
    public String getUrl(Configuration config) {
        String intervieweeRootUrl = config.urlPrefix() + "/" + getBaseName();
        return intervieweeRootUrl + "/transcripts/" + getTargetFileName();
    }
}