package com.huatu.tiku.course.spring.conf.resolver;

import com.google.common.collect.Maps;
import com.huatu.common.utils.proxy.InterfaceHandler;
import com.huatu.common.utils.scan.IScan;
import com.huatu.common.utils.scan.PackageLoaderScan;
import com.huatu.tiku.course.netschool.bean.NetSchoolResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.feign.FeignClient;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author hanchao
 * @date 2017/9/13 9:06
 */
@Slf4j
public class ServiceFallbackGenerator {
    private static AtomicBoolean _lock = new AtomicBoolean(true);
    private static Map<Class,Object> fallbacks = Maps.newHashMap();


    //public static <T> T ge


    static{
        if(_lock.compareAndSet(true,false)){
            IScan scan = new PackageLoaderScan();
            Set<Class<?>> inters = scan.scan("com.huatu.tiku.course.netschool.api", FeignClient.class);

            Map<Class,String> _mapping = Maps.newHashMap();
            _mapping.put(NetSchoolResponse.class," com.huatu.tiku.course.netschool.bean.NetSchoolResponse.DEFAULT ");

            for (Class<?> inter : inters) {
                Object _instance = load(inter,_mapping);
                if(_instance == null){
                    continue;
                }
                fallbacks.put(inter,_instance);
            }
        }else{
            log.warn("proxy already started...");
        }
    }

    private static Object load(Class<?> inter, Map<Class,String> mapping) {
        if (!inter.isInterface()) {
            log.info(inter.getName() + " is not Interface");
            return null;
        }

        if (inter.getAnnotation(FeignClient.class) == null) {
            log.error(inter + "" + "is not annotation RestClient");
            return null;
        }
        InterfaceHandler interfaceHandler = null;
        ServiceLoader<InterfaceHandler> spiLoader = ServiceLoader.load(InterfaceHandler.class);
        for (InterfaceHandler handler : spiLoader) {
            interfaceHandler = handler;
            break;
        }
        try {
            Class<?> clazz = interfaceHandler.generateClass(inter,mapping);
            return clazz.newInstance();
        } catch (Exception e) {
            log.error("get class instance error...",e);
            return null;
        }
    }
}
