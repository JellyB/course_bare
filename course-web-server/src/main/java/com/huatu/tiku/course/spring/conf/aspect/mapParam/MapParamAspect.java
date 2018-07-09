package com.huatu.tiku.course.spring.conf.aspect.mapParam;

import com.huatu.common.utils.collection.HashMapBuilder;
import com.huatu.tiku.common.bean.user.UserSession;
import com.huatu.tiku.springboot.users.service.UserSessionService;
import com.huatu.tiku.springboot.users.support.UserSessionHolder;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 确认在其他数据处理完成之后再进行
 * Created by lijun on 2018/7/6
 */
@Order(Ordered.HIGHEST_PRECEDENCE - 100)
@Component
@Aspect
public class MapParamAspect {

    @Autowired
    private UserSessionService userSessionService;

    /**
     * 需要本地化的 head 请求参数
     */
    private static final String[] HEARD_PARAM = new String[]{"cv", "terminal", "appType"};

    @Pointcut("@annotation(com.huatu.tiku.course.spring.conf.aspect.mapParam.LocalMapParam)")
    private void mapParamMethod() {
    }

    /**
     * 进入方法之前 转换参数
     */
    @Before("mapParamMethod()")
    public void before(JoinPoint joinPoint) {


        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        //1.build Heard 信息
        HashMapBuilder<String, Object> hashMapBuilder = HashMapBuilder.newBuilder();
        for (String headStr : HEARD_PARAM) {
            if (StringUtils.isNotBlank(request.getHeader(headStr))) {
                hashMapBuilder.put(headStr, request.getHeader(headStr));
            }
        }
        //1.2 处理token
        if (StringUtils.isNotBlank(request.getHeader("token"))) {
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            Method method = methodSignature.getMethod();
            //获取当前 token -> userName 方法
            LocalMapParam localMapParam = method.getAnnotation(LocalMapParam.class);
            TokenType tokenType = localMapParam.tokenType();
            String userName = "";
            switch (tokenType) {
                case ZTK:
                    userName = getZTKUserName(request.getHeader("token"));
                    break;
                case IC:
                    break;
            }
            if (StringUtils.isNotBlank(userName)) {
                hashMapBuilder.put("userName", userName);
            }
        }
        //2. build RequesBody
        Map<String, String[]> map = request.getParameterMap();
        Function<String[], String> transParam = (param) -> Arrays.asList(param).stream().collect(Collectors.joining(","));
        for (String entry : map.keySet()) {
            hashMapBuilder.put(entry, transParam.apply(map.get(entry)));
        }
        LocalMapParamHandler.set(hashMapBuilder.build());
    }

    /**
     * 完成之后手动释放
     */
    @AfterReturning(value = "mapParamMethod()")
    public void after() {
        LocalMapParamHandler.clean();
    }

    /**
     * 异常后手动释放
     */
    @AfterThrowing(value = "mapParamMethod()")
    public void exception() {
        LocalMapParamHandler.clean();
    }


    /**
     * 根据 token 获取用户信息 此处主要获取用户名称 需关联start-user
     *
     * @return 用户信息
     */
    private String getZTKUserName(String token) {
        //从当前线程存储获取，如果没有再去redis查找，减少访问次数
        UserSession userSession = UserSessionHolder.get();
        if (userSession == null) {
            userSession = userSessionService.getUserSession(token);
        }
        if (userSession != null) {
            return userSession.getUname();
        }
        return StringUtils.EMPTY;
    }
}
