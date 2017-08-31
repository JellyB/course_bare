package com.huatu.tiku.course.service;

import com.huatu.common.consts.TerminalType;
import com.huatu.tiku.common.consts.VersionRedisKey;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * TODO
 * 后期把这些移到服务里，这里只用到一个查询，引入了完整的redis环境
 * @author hanchao
 * @date 2017/8/29 19:51
 */
@Component
@Deprecated
public class VersionService {
    @Resource(name = "redisTemplate")
    private SetOperations<String,String> setOperations;

    /**
     * 是否审核版本
     * @param catgory 考试类型
     * @return
     */
    @Deprecated
    public boolean isIosAudit(int catgory,int terminal,String cv) {
        if (terminal == TerminalType.IPHONE || terminal == TerminalType.IPHONE_IPAD) {
            String iosAuditSetKey = VersionRedisKey.getIosAuditSetKey(catgory);
            return setOperations.isMember(iosAuditSetKey, cv);
        }
        return false;
    }
}
