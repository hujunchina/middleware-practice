package com.hujunchina.middleware.model.entity;

import java.math.BigDecimal;
import java.util.Date;

public class RedDivide {
    private Integer id;

    private Integer uid;

    private String uuid;

    private BigDecimal money;

    private Date divideTime;

    private Byte isValid;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid == null ? null : uuid.trim();
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public Date getDivideTime() {
        return divideTime;
    }

    public void setDivideTime(Date divideTime) {
        this.divideTime = divideTime;
    }

    public Byte getIsValid() {
        return isValid;
    }

    public void setIsValid(Byte isValid) {
        this.isValid = isValid;
    }
}