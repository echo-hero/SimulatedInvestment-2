package cn.fan.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;//可序列号
import java.util.Date;

@Entity //实体类
@Table(name = "fund_main")
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
public class FundMain implements Serializable {

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

    public String getFundname() {
        return fundname;
    }

    public void setFundname(String fundname) {
        this.fundname = fundname;
    }

    public float getCommission_rate() {
        return commission_rate;
    }

    public void setCommission_rate(float commission_rate) {
        this.commission_rate = commission_rate;
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
    private String fundname;
    private float commission_rate;
    private Date create_date;
    private Date update_date;

}
