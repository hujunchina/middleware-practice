package com.hujunchina.middleware.model.entity;

import java.math.BigDecimal;
import java.util.Date;

public class RedDetail {
    private Integer id;

    private String recordId;

    private BigDecimal perMoney;

    private Byte isValid;

    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId == null ? null : recordId.trim();
    }

    public BigDecimal getPerMoney() {
        return perMoney;
    }

    public void setPerMoney(BigDecimal perMoney) {
        this.perMoney = perMoney;
    }

    public Byte getIsValid() {
        return isValid;
    }

    public void setIsValid(Byte isValid) {
        this.isValid = isValid;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}