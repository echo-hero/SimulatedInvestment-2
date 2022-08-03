package cn.fan.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "task_remind")
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
public class TaskRemind implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFundcode() {
        return fundcode;
    }

    public void setFundcode(String fundcode) {
        this.fundcode = fundcode;
    }

    public int getUpdown_flag() {
        return updown_flag;
    }

    public void setUpdown_flag(int updown_flag) {
        this.updown_flag = updown_flag;
    }

    public float getDwjz() {
        return dwjz;
    }

    public void setDwjz(float dwjz) {
        this.dwjz = dwjz;
    }

    public String getAddressee() {
        return addressee;
    }

    public void setAddressee(String addressee) {
        this.addressee = addressee;
    }

    public int getIsnotice() {
        return isnotice;
    }

    public void setIsnotice(int isnotice) {
        this.isnotice = isnotice;
    }

    public int getEmail_id() {
        return email_id;
    }

    public void setEmail_id(int email_id) {
        this.email_id = email_id;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public Date getCreate_date() {
        return create_date;
    }

    public void setCreate_date(Date create_date) {
        this.create_date = create_date;
    }

    public Date getUpdate_date() {
        return update_date;
    }

    public void setUpdate_date(Date update_date) {
        this.update_date = update_date;
    }

    private int id;
    private String fundcode;
    private int updown_flag;
    private float dwjz;
    private String addressee;
    private int isnotice;
    private int email_id;
    private int userid;
    private Date create_date;
    private Date update_date;

}
