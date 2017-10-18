package com.huatu.tiku.course.service;

import com.huatu.springboot.degrade.core.Degrade;
import com.huatu.tiku.course.bean.NetSchoolResponse;
import com.huatu.tiku.course.netschool.api.fall.CourseServiceV3Fallback;
import com.huatu.tiku.course.netschool.api.v3.CourseServiceV3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author hanchao
 * @date 2017/10/18 10:38
 */
@Service
public class CourseCollectionBizService {
    @Autowired
    private CourseServiceV3 courseServiceV3;
    @Autowired
    private CourseServiceV3Fallback courseServiceV3Fallback;

    @Degrade(key = "collectionCourse",name = "课程合集")
    public NetSchoolResponse getCollectionCourse(String shorttitle,String uname,int page){
        NetSchoolResponse collectionDetail = courseServiceV3.getCollectionDetail(shorttitle, uname, page);
        courseServiceV3Fallback.setCollectionDetail(shorttitle,uname, page,collectionDetail);
        return collectionDetail;
    }

    public NetSchoolResponse getCollectionCourseDegrade(String shorttitle,String uname,int page){
        NetSchoolResponse collectionDetail = courseServiceV3Fallback.getCollectionDetail(shorttitle, uname, page);
        if(collectionDetail.getCode() == NetSchoolResponse.DEFAULT_ERROR.getCode()){ //说明数据是mock过来的，放单个线程过去构建此数据

        }
        return collectionDetail;
    }
}
