package com.huatu.tiku.course.common;

import com.huatu.common.consts.TerminalType;

/**
 * @author hanchao
 * @date 2017/9/13 16:54
 */
public class NetSchoolTerminalType {
    private static final int ANDROID = 0;
    private static final int IOS = 1;

    public static int transform(int terminal){
        if (terminal == TerminalType.ANDROID || terminal == TerminalType.ANDROID_IPAD) {
            return ANDROID;
        } else if (terminal == TerminalType.IPHONE || terminal == TerminalType.IPHONE_IPAD) {
            return IOS;
        }
        return ANDROID;
    }
}
