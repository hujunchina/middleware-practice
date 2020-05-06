package com.hujunchina.middleware.server.controller.redis;

import com.hujunchina.middleware.server.service.redis.CachePassService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

//  控制类
@RestController
public class CachePassController {
    private static final Logger log = LoggerFactory.getLogger(CachePassController.class);
    private static final String prefix = "cache/pass";
//    服务类
    @Autowired
    private CachePassService cachePassService;

    @RequestMapping(value = prefix+"/item/info", method = RequestMethod.GET)
    public Map<String, Object> getItem(@RequestParam String itemCode){
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("code", 0);
        resMap.put("msg", "success");
        try {
            resMap.put("data", cachePassService.getItemInfo(itemCode));
        }catch (Exception e){
            resMap.put("code", -1);
            resMap.put("msg", "failed:"+e.getMessage());
        }
        return resMap;
    }
}
