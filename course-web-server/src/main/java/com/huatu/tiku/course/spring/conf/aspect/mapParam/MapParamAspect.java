package com.huatu.tiku.course.spring.conf.aspect.mapParam;

import com.huatu.common.ErrorResult;
import com.huatu.common.exception.BizException;
import com.huatu.common.utils.collection.HashMapBuilder;
import com.huatu.tiku.common.bean.user.UserSession;
import com.huatu.tiku.springboot.users.service.UserSessionService;
import com.huatu.tiku.springboot.users.support.UserSessionHolder;
import javassist.*;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.*;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;

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

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 需要本地化的 head 请求参数
     */
    private static final String[] HEARD_PARAM = new String[]{"cv", "terminal", "appType"};

    /**
     * 参数中排除的对象
     */
    private static final ArrayList EXCEPT_PARAM = new ArrayList() {{
        add(UserSession.class);
    }};

    /**
     * 面库 redis key 前缀
     */
    private static final String IC_REDIS_KEY_PREFIX = "ic.";

    /**
     * 验证失败 消息
     */
    private static final ErrorResult FAIL_RESULT = ErrorResult.create(1110002, "用户会话过期");

    @Pointcut("@annotation(com.huatu.tiku.course.spring.conf.aspect.mapParam.LocalMapParam)")
    private void mapParamMethod() {
    }

    /**
     * 进入方法之前 转换参数
     */
    @Before("mapParamMethod()")
    public void before(JoinPoint joinPoint) throws NotFoundException, ClassNotFoundException {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        //1.build Heard 信息
        HashMapBuilder<String, Object> hashMapBuilder = HashMapBuilder.newBuilder();
        //1.1 处理token - 可能需要验证 token
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        //获取当前 token -> userName 方法
        LocalMapParam localMapParam = method.getAnnotation(LocalMapParam.class);
        if (StringUtils.isNotBlank(request.getHeader("token"))) {
            if (localMapParam.needUserName()) {
                TokenType tokenType = localMapParam.tokenType();
                switch (tokenType) {
                    case ZTK:
                        String userName = getZTKUserName(request.getHeader("token"));
                        if (localMapParam.checkToken() && StringUtils.isBlank(userName)) {
                            throw new BizException(FAIL_RESULT);
                        }
                        if (StringUtils.isNotBlank(userName)) {
                            hashMapBuilder.put("userName", userName);
                        }
                        break;
                    case IC:
                        String userId = getICUserId(request.getHeader("token"));
                        if (localMapParam.checkToken() && StringUtils.isBlank(userId)) {
                            throw new BizException(FAIL_RESULT);
                        }
                        if (StringUtils.isNotBlank(userId)) {
                            hashMapBuilder.put("userId", userId);
                        }
                        break;
                    default:
                        //类型非法
                        throw new BizException(ErrorResult.create(1110003, "用户信息非法"));

                }
            }
        } else if (localMapParam.checkToken()) {
            throw new BizException(FAIL_RESULT);
        }
        //1.2 处理其他的head 信息
        for (String headStr : HEARD_PARAM) {
            if (StringUtils.isNotBlank(request.getHeader(headStr))) {
                hashMapBuilder.put(headStr, request.getHeader(headStr));
            }
        }
        //2. build RequestBody
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        String[] fileName = getFileName(className, methodName);
        if (null != fileName) {
            for (int index = 0; index < fileName.length; index++) {
                if (!EXCEPT_PARAM.contains(args[index].getClass())) {
                    hashMapBuilder.put(fileName[index], args[index]);
                }
            }
        }
//        Map<String, String[]> map = request.getParameterMap();
//        if (null != map) {
//            Function<String[], String> transParam = (param) -> Arrays.asList(param).stream().collect(Collectors.joining(","));
//            for (String entry : map.keySet()) {
//                hashMapBuilder.put(entry, transParam.apply(map.get(entry)));
//            }
//        }
        //3. build PathVariable
        NativeWebRequest nativeWebRequest = new ServletWebRequest(request);
        Map<String, String> pathParam = (Map<String, String>) nativeWebRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
        if (null != pathParam) {
            for (String key : pathParam.keySet()) {
                //此处不需要做 notBlank 判断，在路径上的参数 必定不会null
                hashMapBuilder.put(key, pathParam.get(key));
            }
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
     * @return 用户名称
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

    /**
     * 获取IC(面库用户ID)
     *
     * @return 用户ID
     */
    private String getICUserId(String token) {
        RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
        try {
            Map<byte[], byte[]> map = connection.hGetAll((IC_REDIS_KEY_PREFIX + token).getBytes());
            if (null == map) {
                return StringUtils.EMPTY;
            }
            byte[] bytes = map.get("id".getBytes());
            if (null == bytes) {
                return StringUtils.EMPTY;
            }
            String userId = new String(bytes);
            if (StringUtils.isNotBlank(userId)) {
                return userId.startsWith("\"") ? userId.substring(0, userId.length() - 1) : userId;
            }
            return userId;
        } finally {
            connection.close();
        }
    }

    /**
     * 获取字段名称
     *
     * @param className  类名
     * @param methodName 方法名
     */
    private String[] getFileName(String className, String methodName) throws ClassNotFoundException, NotFoundException {
        Class<?> clazz = Class.forName(className);
        ClassPool classPool = ClassPool.getDefault();
        ClassClassPath classPath = new ClassClassPath(clazz);
        classPool.insertClassPath(classPath);

        //获取类信息
        String clazzName = clazz.getName();
        CtClass ctClass = classPool.get(clazzName);
        CtMethod declaredMethod = ctClass.getDeclaredMethod(methodName);
        MethodInfo methodInfo = declaredMethod.getMethodInfo();
        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
        LocalVariableAttribute attribute = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
        if (null == attribute) {
            return null;
        }
        String[] paramsArgsName = new String[declaredMethod.getParameterTypes().length];
        int pos = Modifier.isStatic(declaredMethod.getModifiers()) ? 0 : 1;
        for (int index = 0; index < paramsArgsName.length; index++) {
            paramsArgsName[index] = attribute.variableName(index + pos);
        }
        return paramsArgsName;
    }
}
