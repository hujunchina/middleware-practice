package com.hujunchina.middleware.server.controller;

import com.hujunchina.middleware.api.BaseResponse;
import com.hujunchina.middleware.api.StatusCode;
import com.hujunchina.middleware.server.entity.RedPacket;
import com.hujunchina.middleware.server.service.IRedPacketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class RedPacketController {
    private static final Logger log = LoggerFactory.getLogger(RedPacketController.class);
    private static final String prefix = "/redpacket";

    @Autowired
    private IRedPacketService redPacketService;

//    RedPacket 一定要有空构造方法，因为 fastjson 要反序列化会用到
    @RequestMapping(value = prefix+"/handout", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BaseResponse handOut(@Validated @RequestBody RedPacket redPacket, BindingResult result){
        System.out.println("--------------in------------------");
        if(result.hasErrors()){
            return new BaseResponse(StatusCode.InvalidPrams);
        }
        log.info("---{}--{}---{}---", redPacket.getUid(),redPacket.getAmount(), redPacket.getTotal());
        BaseResponse response = new BaseResponse(StatusCode.Success);
        try{
            String redID = redPacketService.handOut(redPacket);
            response.setData(redID);
        }catch (Exception e){
            log.error("发红包异常：{}", e.toString());
            response = new BaseResponse(StatusCode.Fail);
        }
        return response;
    }
}
