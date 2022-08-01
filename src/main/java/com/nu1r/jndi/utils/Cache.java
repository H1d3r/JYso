package com.nu1r.jndi.utils;

import com.nu1r.jndi.template.*;
import com.nu1r.jndi.template.Weblogic.WeblogicMemshellTemplate1;
import com.nu1r.jndi.template.Weblogic.WeblogicMemshellTemplate2;
import com.nu1r.jndi.template.Websphere.WebsphereMemshellTemplate;
import com.nu1r.jndi.template.jboss.JBFMSFromContextF;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import java.util.concurrent.TimeUnit;

public class Cache {
    private static ExpiringMap<String, byte[]> map = ExpiringMap.builder()
            .maxSize(1000)
            .expiration(30, TimeUnit.SECONDS)
            .variableExpiration()
            .expirationPolicy(ExpirationPolicy.CREATED)
            .build();

    static{
        try {
            //过期时间100年，永不过期的简单方法
            map.put("TomcatEchoTemplate", Util.getClassBytes(TomcatEchoTemplate.class), 365 * 100, TimeUnit.DAYS);
            map.put("SpringEchoTemplate", Util.getClassBytes(SpringEchoTemplate.class), 365 * 100, TimeUnit.DAYS);
            map.put("WeblogicEchoTemplate", Util.getClassBytes(WeblogicEchoTemplate.class), 365 * 100, TimeUnit.DAYS);
            map.put("WeblogicMemshellTemplate1", Util.getClassBytes(WeblogicMemshellTemplate1.class), 365 * 100, TimeUnit.DAYS);
            map.put("WeblogicMemshellTemplate2", Util.getClassBytes(WeblogicMemshellTemplate2.class), 365 * 100, TimeUnit.DAYS);
            map.put("JBossMemshellTemplate", Util.getClassBytes(JBFMSFromContextF.class), 365 * 100, TimeUnit.DAYS);
            map.put("WebsphereMemshellTemplate", Util.getClassBytes(WebsphereMemshellTemplate.class), 365 * 100, TimeUnit.DAYS);
            map.put("isOK", Util.getClassBytes(isOK.class), 365 * 100, TimeUnit.DAYS);
            //测试添加到cache中
            map.put("isSuccess",Util.getClassBytes(isSuccess.class),365 * 100, TimeUnit.DAYS);
            map.put("Meterpreter",ClassByteChange.update(Meterpreter.class),365 * 100, TimeUnit.DAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] get(String key){
        return map.get(key);
    }

    public static void set(String key, byte[] bytes){
        map.put(key, bytes);
    }

    public static boolean contains(String key){
        return map.containsKey(key);
    }

    public static void remove(String key){
        map.remove(key);
    }
}
