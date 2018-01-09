package com.huatu.tiku.course.web.controller.v3;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.huatu.common.spring.web.MediaType;
import com.huatu.common.utils.collection.HashMapBuilder;
import com.huatu.tiku.course.bean.NetSchoolResponse;
import com.huatu.tiku.course.consts.NetschoolTerminalType;
import com.huatu.tiku.course.netschool.api.v3.OrderServiceV3;
import com.huatu.tiku.course.netschool.api.v3.PromoteCoreServiceV3;
import com.huatu.tiku.course.util.RequestUtil;
import com.huatu.tiku.course.util.ResponseUtil;
import com.huatu.tiku.springboot.users.bean.UserSession;
import com.huatu.tiku.springboot.users.support.Token;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hanchao
 * @date 2017/9/14 16:31
 */
@RestController
@RequestMapping("/v3/orders")
public class OrderControllerV3 {
    @Autowired
    private OrderServiceV3 orderServiceV3;
    @Autowired
    private PromoteCoreServiceV3 promoteCoreServiceV3;

    @Autowired
    private OkHttpClient okHttpClient;

    @Value("${seckill.pay.url}")
    private String seckillPayUrl;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 下单页面相关信息（结算信息，收货地址）
     * @param rid
     * @param userSession
     * @return
     */
    @GetMapping("/previnfo")
    public Object getPrevInfo(@RequestParam int rid,
                              @Token UserSession userSession) {
        Map<String,Object> params = Maps.newHashMap();
        params.put("rid",rid);
        params.put("action","placeOrder");
        params.put("username",userSession.getUname());
        return ResponseUtil.build(promoteCoreServiceV3.getPrevInfo(RequestUtil.encrypt(params)),true);
    }

    /**
     * 添加免费课程
     * @param terminal 终端类型
     * @param courseId 课程id
     * @return
     * @throws Exception
     */
    @PostMapping("/free")
    public Object freeCourse(@RequestHeader int terminal,
                             @RequestParam int courseId,
                             @Token UserSession userSession) throws Exception {
        String uname = userSession.getUname();
        int catgory = userSession.getCategory();
        final HashMap<String, Object> parameterMap = HashMapBuilder.newBuilder()
                .put("username", uname)
                .put("source", NetschoolTerminalType.transform(terminal))
                .put("userid", -1)
                .put("rid", courseId)
                .buildUnsafe();
        return ResponseUtil.build(orderServiceV3.getFree(RequestUtil.encryptJsonParams(parameterMap)));
    }


    /**
     * 创建订单
     * @param addressid
     * @param rid
     * @param fromuser
     * @param tjCode
     * @param userSession
     * @return
     */
    @PostMapping("/create")
    public Object createOrder(@RequestParam String addressid,
                              @RequestParam int rid,
                              @RequestParam(required = false,defaultValue = "0") String fromuser,
                              @RequestParam String tjCode,
                              @RequestParam(required = false) String FreeCardID,
                              @RequestHeader int terminal,
                              @Token UserSession userSession) {
        Map<String,Object> params = Maps.newHashMap();
        params.put("action","createOrder");
        params.put("addressid",addressid);
        params.put("FreeCardID",FreeCardID);
        params.put("fromuser",fromuser);
        params.put("rid",rid);
        params.put("source",(terminal == 2 || terminal == 5)?'I':'A');//不是ios，就传android
        params.put("tjCode",tjCode);
        params.put("username",userSession.getUname());

        return ResponseUtil.build(promoteCoreServiceV3.createOrder(RequestUtil.encrypt(params)),true);
    }

    /**
     * 订单详情
     * @param orderNo
     * @return
     */
    @GetMapping("/{orderNo}")
    public Object getOrderDetail(@PathVariable String orderNo,
                                 @RequestParam String type) {
        Map<String,Object> params = HashMapBuilder.<String,Object>newBuilder()
                .put("action","orderDetail")
                .put("ordernum",orderNo)
                .put("type",type)
                .build();
        return ResponseUtil.build(promoteCoreServiceV3.getOrderDetail(RequestUtil.encrypt(params)),true);
    }


    /**
     * 取消订单
     * @param orderNo
     * @return
     */
    @PostMapping("/{orderNo}/cancel")
    public Object cancelOrder(@PathVariable String orderNo) {
        Map<String,Object> params = HashMapBuilder.<String,Object>newBuilder()
                .put("action","cancel")
                .put("ordernum",orderNo)
                .build();
        return ResponseUtil.build(orderServiceV3.cancelOrder(RequestUtil.encrypt(params)),true);
    }

    /**
     * 我的订单列表
     * @param session
     * @param page
     * @param type
     * @return
     */
    @GetMapping("/my")
    public Object myOrders(@Token UserSession session,
                           @RequestParam int page,
                           @RequestParam int type) {
        Map<String,Object> params = HashMapBuilder.<String,Object>newBuilder()
                .put("action","myOrder")
                .put("page",page)
                .put("type",type)
                .put("username",session.getUname())
                .build();
        return ResponseUtil.build(orderServiceV3.myOrder(RequestUtil.encrypt(params)),true);
    }


    /**
     * 支付订单
     * @return
     */
    @PostMapping("/{orderNo}/pay")
    public Object payOrder(@PathVariable String orderNo,
                           @RequestParam int payment,
                           @Token UserSession session) {
        Map<String,Object> params = HashMapBuilder.<String,Object>newBuilder()
                .put("action","pay")
                .put("ordernum",orderNo)
                .put("payment",payment)
                .put("username",session.getUname())
                .build();
        return ResponseUtil.build(promoteCoreServiceV3.payOrder(RequestUtil.encrypt(params)),true);
    }


    /**
     * 秒杀的支付方式
     * @param ordernum
     * @param userSession
     * @return
     * @throws IOException
     */
    @PostMapping("/seckill/payWay")
    public Object payWay(@RequestParam String ordernum,
                           @Token UserSession userSession) throws IOException {
        Map<String,Object> params = HashMapBuilder.<String,Object>newBuilder()
                .put("action","payWay")
                .put("ordernum",ordernum)
                .build();
        String p = RequestUtil.encrypt(params);
        okhttp3.RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse(MediaType.APPLICATION_FORM_URLENCODED_UTF8_VALUE), "p="+p);
        Request request = new Request.Builder().url(seckillPayUrl).post(requestBody).build();
        Response responseBody = okHttpClient.newCall(request).execute();
        NetSchoolResponse response = objectMapper.readValue(responseBody.body().string(),NetSchoolResponse.class);
        return ResponseUtil.build(response,true);
    }

    /**
     * 秒杀支付
     * @param ordernum
     * @param payment
     * @param userSession
     * @return
     * @throws IOException
     */
    @PostMapping("/seckill/pay")
    public Object payOrder(@RequestParam String ordernum,
                           @RequestParam String payment,
                           @Token UserSession userSession) throws IOException {
        Map<String,Object> params = HashMapBuilder.<String,Object>newBuilder()
                .put("action","pay")
                .put("ordernum",ordernum)
                .put("payment",payment)
                .build();
        String p = RequestUtil.encrypt(params);
        okhttp3.RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse(MediaType.APPLICATION_FORM_URLENCODED_UTF8_VALUE), "p="+p);
        Request request = new Request.Builder().url(seckillPayUrl).post(requestBody).build();
        Response responseBody = okHttpClient.newCall(request).execute();
        NetSchoolResponse response = objectMapper.readValue(responseBody.body().string(),NetSchoolResponse.class);
        return ResponseUtil.build(response,true);
    }

}
