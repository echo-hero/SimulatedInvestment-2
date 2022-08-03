package cn.fan.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "sys_power")
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
public class SysPower implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPowername() {
        return powername;
    }

    public void setPowername(String powername) {
        this.powername = powername;
    }

    public String getPowerdesc() {
        return powerdesc;
    }

    public void setPowerdesc(String powerdesc) {
        this.powerdesc = powerdesc;
    }

    public String getPowerurl() {
        return powerurl;
    }

    public void setPowerurl(String powerurl) {
        this.powerurl = powerurl;
    }

    public Date getCreate_date() {
        return create_date;
    }

    public void setCreate_date(Date create_date) {
        this.create_date = create_date;
    }

    public int getCreate_by() {
        return create_by;
    }

    public void setCreate_by(int create_by) {
        this.create_by = create_by;
    }

    public Date getUpdate_date() {
        return update_date;
    }

    public void setUpdate_date(Date update_date) {
        this.update_date = update_date;
    }

    public int getUpdate_by() {
        return update_by;
    }

    public void setUpdate_by(int update_by) {
        this.update_by = update_by;
    }

    private int id;
    private String powername;
    private String powerdesc;
    private String powerurl;
    private Date create_date;
    private int create_by;
    private Date update_date;
    private int update_by;
}
