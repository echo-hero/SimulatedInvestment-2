package cn.fan.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "user_invest")
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
public class UserInvest implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getFundcode() {
        return fundcode;
    }

    public void setFundcode(String fundcode) {
        this.fundcode = fundcode;
    }

    public int getBuysell_flag() {
        return buysell_flag;
    }

    public void setBuysell_flag(int buysell_flag) {
        this.buysell_flag = buysell_flag;
    }

    public String getOperate_date() {
        return operate_date;
    }

    public void setOperate_date(String operate_date) {
        this.operate_date = operate_date;
    }

    public float getShare() {
        return share;
    }

    public void setShare(float share) {
        this.share = share;
    }

    public float getDwjz() {
        return dwjz;
    }

    public void setDwjz(float dwjz) {
        this.dwjz = dwjz;
    }

    public float getCommission() {
        return commission;
    }

    public void setCommission(float commission) {
        this.commission = commission;
    }

    public float getSum_money() {
        return sum_money;
    }

    public void setSum_money(float sum_money) {
        this.sum_money = sum_money;
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
    private int userid;
    private String fundcode;
    private int buysell_flag;
    private String operate_date;
    private float share;
    private float dwjz;
    private float commission;
    private float sum_money;
    private Date create_date;
    private Date update_date;

}
