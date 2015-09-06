package uk.ac.triendo.myhome;

/**
 * Created by SHARC on 06/09/2015.
 */
public class ReturnMessage {
    private long id;

    private String subject;

    private String text;

    public ReturnMessage() {
    }

    public ReturnMessage(long id, String subject, String text) {
        this.id = id;
        this.subject = subject;
        this.text = text;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public String toString() {
        return "Id:[" + this.getId() + "] Subject:[" + this.getSubject() + "] Text:[" + this.getText() + "]";
    }
}