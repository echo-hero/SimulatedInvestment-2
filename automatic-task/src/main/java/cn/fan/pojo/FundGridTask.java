package cn.fan.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity //实体类
@Table(name = "fund_grid_task")
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
public class FundGridTask implements Serializable {

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

    public float getInit_dwjz() {
        return init_dwjz;
    }

    public void setInit_dwjz(float init_dwjz) {
        this.init_dwjz = init_dwjz;
    }

    public float getPer_grid_money() {
        return per_grid_money;
    }

    public void setPer_grid_money(float per_grid_money) {
        this.per_grid_money = per_grid_money;
    }

    public float getGrid_rate() {
        return grid_rate;
    }

    public void setGrid_rate(float grid_rate) {
        this.grid_rate = grid_rate;
    }

    public float getGrid_step() {
        return grid_step;
    }

    public void setGrid_step(float grid_step) {
        this.grid_step = grid_step;
    }

    public int getRise_taskid() {
        return rise_taskid;
    }

    public void setRise_taskid(int rise_taskid) {
        this.rise_taskid = rise_taskid;
    }

    public int getFall_taskid() {
        return fall_taskid;
    }

    public void setFall_taskid(int fall_taskid) {
        this.fall_taskid = fall_taskid;
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
    private float init_dwjz;
    private float per_grid_money;
    private float grid_rate;
    private float grid_step;
    private int rise_taskid;
    private int fall_taskid;
    private Date create_date;
    private Date update_date;

}
