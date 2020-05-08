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
import java.math.BigDecimal;

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

//    抢红包 调度
    @RequestMapping(value = prefix+"/rob", method = RequestMethod.GET)
    public BaseResponse rob(@RequestParam Integer uid, @RequestParam String redID){
        BaseResponse response = new BaseResponse(StatusCode.Success);
        try{
            BigDecimal result = redPacketService.rob(uid, redID);
            if( result!=null ){
                response.setData(result);
            }else{
                response = new BaseResponse(StatusCode.Fail.getCode(), "红包被抢完了");
            }
        }catch (Exception e){
            log.error("抢红包异常: userID = {}, redID = {}", uid,redID, e.fillInStackTrace());
            response = new BaseResponse(StatusCode.Fail.getCode(), e.getMessage());
        }
        return response;
    }
}
