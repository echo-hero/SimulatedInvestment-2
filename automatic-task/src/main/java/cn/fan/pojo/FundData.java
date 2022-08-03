package cn.fan.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "fund_data")
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
public class FundData implements Serializable {

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

    public String getFunddate() {
        return funddate;
    }

    public void setFunddate(String funddate) {
        this.funddate = funddate;
    }

    public float getDwjz() {
        return dwjz;
    }

    public void setDwjz(float dwjz) {
        this.dwjz = dwjz;
    }

    public Date getCreate_date() {
        return create_date;
    }

    public void setCreate_date(Date create_date) {
        this.create_date = create_date;
    }

    private int id;
    private String fundcode;
    private String funddate;
    private float dwjz;
    private Date create_date;

}
