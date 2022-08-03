package cn.fan.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "email_record")
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
public class EmailRecord implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMail_theme() {
        return mail_theme;
    }

    public void setMail_theme(String mail_theme) {
        this.mail_theme = mail_theme;
    }

    public String getMail_content() {
        return mail_content;
    }

    public void setMail_content(String mail_content) {
        this.mail_content = mail_content;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getAddressee() {
        return addressee;
    }

    public void setAddressee(String addressee) {
        this.addressee = addressee;
    }

    public int getIssuccess() {
        return issuccess;
    }

    public void setIssuccess(int issuccess) {
        this.issuccess = issuccess;
    }

    public String getFailure_cause() {
        return failure_cause;
    }

    public void setFailure_cause(String failure_cause) {
        this.failure_cause = failure_cause;
    }

    public Date getCreate_date() {
        return create_date;
    }

    public void setCreate_date(Date create_date) {
        this.create_date = create_date;
    }

    private int id;
    private String mail_theme;
    private String mail_content;
    private int userid;
    private String addressee;
    private int issuccess;
    private String failure_cause;
    private Date create_date;

}
