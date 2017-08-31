package com.huatu.tiku.course.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.huatu.tiku.common.consts.CatgoryType;
import com.huatu.tiku.course.common.AuditListType;
import com.huatu.tiku.course.common.NetSchoolConfig;
import com.huatu.tiku.course.netschool.api.UserCoursesServiceV1;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 适配ios审核
 * @author hanchao
 * @date 2017/8/30 10:18
 */
@Service
public class IosBffService {
    private final static int DEFAULT_ORDER = 1000;

    private static Map<String,Object> COURSE_LIST_MOCK = Maps.newHashMap();
    private static Map<String,Object> BOOK_LIST_MOCK = Maps.newHashMap();
    private static Map<String,String> DETAIL_FILES = Maps.newHashMap();
    private static Set<Integer> BOOK_IDS = Sets.newHashSet();


    @Autowired
    private UserCoursesServiceV1 userCoursesService;

    static {
        try {
            String courseTempalte = FileUtils.readFileToString(new File(IosBffService.class.getResource("/templates/mock/ios_audit_list.json").getFile()));
            JSONObject json = JSON.parseObject(courseTempalte);
            COURSE_LIST_MOCK = json;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static {
        try {
            String courseTempalte = FileUtils.readFileToString(new File(IosBffService.class.getResource("/templates/mock/ios_audit_book_list.json").getFile()));
            JSONObject json = JSON.parseObject(courseTempalte);
            BOOK_LIST_MOCK = json;

            for (Object data : (JSONArray) BOOK_LIST_MOCK.get("result")) {
                if(data instanceof JSONObject){
                    BOOK_IDS.add(((JSONObject) data).getInteger("rid"));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static {
        try {
            File [] templates = new File(IosBffService.class.getResource("/templates/mock").toURI()).listFiles(f -> f.getName().endsWith(".html"));
            for (File template : templates) {
                DETAIL_FILES.put(template.getName(),FileUtils.readFileToString(template));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String getTemplateByName(String name){
        return DETAIL_FILES.get(name);
    }


    /**
     * 我的图书或直播列表
     * @param username
     * @param order
     * @param catgory
     * @param listType
     * @return
     * @throws Exception
     */
    public LinkedHashMap getMyList(String username, int order, int catgory, int listType) throws Exception {
        final HashMap<String, Object> params = Maps.newHashMap();
        params.put("username", username);
        params.put("order", order);
        params.put("categoryid", catgory == CatgoryType.GONG_WU_YUAN ?
                NetSchoolConfig.CATEGORY_GWY : NetSchoolConfig.CATEGORY_SHIYE);
        LinkedHashMap data = (LinkedHashMap)userCoursesService.myList(params).getData() ;
        return filterData(data, listType);
    }

    /**
     * 图书/直播列表
     * @param username
     * @param catgory
     * @param listType
     * @return
     * @throws Exception
     */
    public Object getList(String username, int catgory, int listType) throws Exception{
        Map listDataMap = null;
        switch (listType){
            case AuditListType.BOOK:
                //需要深拷贝
                if(BOOK_LIST_MOCK instanceof JSONObject){
                    listDataMap = JSON.parseObject(BOOK_LIST_MOCK.toString());
                }else{
                    listDataMap = JSON.parseObject(JSON.toJSONString(BOOK_LIST_MOCK));
                }
                break;
            case AuditListType.LIVE:
                if(COURSE_LIST_MOCK instanceof JSONObject){
                    listDataMap = JSON.parseObject(COURSE_LIST_MOCK.toString());
                }else{
                    listDataMap = JSON.parseObject(JSON.toJSONString(COURSE_LIST_MOCK));
                }
                break;
            default:
                throw new IllegalArgumentException("wrong listtype");
        }


        //查询我的直播
        LinkedHashMap myList = getMyList(username, DEFAULT_ORDER, catgory, listType);
        ArrayList<Map> myListResult = (ArrayList<Map>)myList.get("result");
        //已经购买的图书或者课程id
        ArrayList<Integer> buyIds = new ArrayList<>();
        for (Map item : myListResult) {
            Integer netClassId = Integer.valueOf(item.get("NetClassId").toString());
            buyIds.add(netClassId);
        }

        ArrayList<Map> listResult = (ArrayList<Map>)listDataMap.get("result");
        ArrayList<Map> newResult = new ArrayList<>();
        for (Map item : listResult) {
            Integer netClassId = Integer.valueOf(item.get("NetClassId").toString());
            if (buyIds.contains(netClassId)) {
                //状态设置成已经购买的
                item.put("isBuy", 1);
            }
            newResult.add(item);
        }

        listDataMap.put("result", newResult);

        return listDataMap;
    }

    //------------------------------------------------------------------------------------------------------------

    /**
     * 过滤数据
     * @param data
     * @param listType
     * @return
     * @throws Exception
     */
    private LinkedHashMap filterData(LinkedHashMap data, int listType) throws Exception{
        ArrayList<Map> result = (ArrayList<Map>)data.get("result");

        ArrayList<Map> newResult = new ArrayList<>();
        for (Map item : result) {
            Integer netClassId = Integer.valueOf(item.get("NetClassId").toString());

            if (listType == AuditListType.BOOK && BOOK_IDS.contains(netClassId)) {
                item.put("startDate","");
                item.put("endDate","");
                //如果是图书列表,而且id是图书id
                newResult.add(item);
            } else if (listType == AuditListType.LIVE && !BOOK_IDS.contains(netClassId)) {
                //如果是直播课列表,而且id不是图书id
                newResult.add(item);
            }
        }
        data.put("result", newResult);

        return data;
    }
}
