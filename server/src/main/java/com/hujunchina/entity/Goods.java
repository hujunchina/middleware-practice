package com.hujunchina.entity;
import lombok.Data;

@Data
public class Goods {

    private Integer goodsNo;
    private String goodsName;

    public Goods(){}
    public Goods(Integer goodsNo, String goodsName){
        this.goodsNo = goodsNo;
        this.goodsName = goodsName;
    }
}
