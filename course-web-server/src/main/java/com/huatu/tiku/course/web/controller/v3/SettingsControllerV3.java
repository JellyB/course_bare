package com.huatu.tiku.course.web.controller.v3;

import com.huatu.common.exception.BizException;
import com.huatu.tiku.common.consts.CatgoryType;
import com.huatu.tiku.course.netschool.api.v3.CourseSettingServiceV3;
import com.huatu.tiku.course.util.ResponseUtil;
import com.huatu.tiku.springboot.users.bean.UserSession;
import com.huatu.tiku.springboot.users.support.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.huatu.tiku.course.util.CourseCKConst.LIVE_SETTINGS;
import static com.huatu.tiku.course.util.CourseCKConst.RECORDING_SETTINGS;

/**
 * @author hanchao
 * @date 2017/9/13 21:19
 */
@RestController
@RequestMapping("/v3")
public class SettingsControllerV3 {
    @Resource(name = "redisTemplate")
    private ValueOperations valueOperations;
    @Autowired
    private CourseSettingServiceV3 courseSettingServiceV3;

    /**
     * 获取直播查询配置
     * @param userSession
     * @return
     * @throws BizException
     */
    @GetMapping("/live/query/_settings")
    public Object getLiveSettings(@Token UserSession userSession) throws BizException {
        String cacheKey = LIVE_SETTINGS;
        Object result = valueOperations.get(cacheKey);
        if(result == null){
            result = ResponseUtil.build(courseSettingServiceV3.getLiveSettings());
            valueOperations.set(cacheKey,result,1, TimeUnit.DAYS);
        }
        return result;
    }

    /**
     * 获取录播查询配置
     * @param userSession
     * @return
     * @throws BizException
     */
    @GetMapping("/recording/query/_settings")
    public Object getRecordingSettings(@Token UserSession userSession) throws BizException {
        String cacheKey = RECORDING_SETTINGS;
        Object result = valueOperations.get(cacheKey);
        if(result == null){
            Map settings = (Map) ResponseUtil.build(courseSettingServiceV3.getRecordingSettings());
            Map<String,Object> categories = (Map<String, Object>) settings.get("category");
            String selKey = "";
            switch (userSession.getCategory()){
                case CatgoryType.GONG_WU_YUAN:
                    selKey = "公务员";
                    break;
                case CatgoryType.SHI_YE_DAN_WEI:
                    selKey = "事业单位";
                    break;
                case CatgoryType.JIAO_SHI:
                    selKey = "教师";
                    break;
                case CatgoryType.JIN_RONG:
                    selKey = "金融";
                    break;
                case CatgoryType.YI_LIAO:
                    selKey = "医疗";
                    break;
            }
            settings.put("category",categories.get(selKey));
            settings.remove("province");//省份的由用户自己选择

            valueOperations.set(cacheKey,settings,1,TimeUnit.DAYS);

            return settings;
        }
        return result;
    }

}
