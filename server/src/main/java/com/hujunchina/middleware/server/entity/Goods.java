package com.hujunchina.middleware.server.entity;
import lombok.Data;

import java.io.Serializable;

@Data
public class Goods implements Serializable {

    private Integer goodsNo;
    private String goodsName;

    public Goods(){}
    public Goods(Integer goodsNo, String goodsName){
        this.goodsNo = goodsNo;
        this.goodsName = goodsName;
    }
}
