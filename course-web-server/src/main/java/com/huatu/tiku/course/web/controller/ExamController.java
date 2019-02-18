package com.huatu.tiku.course.web.controller;


import com.huatu.tiku.course.bean.NetSchoolResponse;
import com.huatu.tiku.course.netschool.api.ExamNetSchoolService;
import com.huatu.tiku.course.util.ResponseUtil;
import com.huatu.tiku.springboot.users.service.UserSessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * @创建人 lizhenjuan
 * @创建时间 2019/2/15
 * @描述 备考精华
 */

@RestController
@RequestMapping(value = "v1/exam")
@Slf4j
public class ExamController {


    @Autowired
    ExamNetSchoolService examNetSchoolService;

    /**
     * 获取备考精华文章列表
     *
     * @param type
     * @param page
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "{type}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object getArticleList(@PathVariable int type,
                                 @RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "10") int pageSize) {
        HashMap param = new HashMap();
        param.put("type", type);
        param.put("page", page);
        param.put("pageSize", pageSize);
        return ResponseUtil.build(examNetSchoolService.getArticleList(param));
    }

    /**
     * 获取文章详情
     *
     * @param aid
     * @return
     */
    @GetMapping(value = "detail/{aid}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object detail(@PathVariable int aid) {
        HashMap paramMap = new HashMap();

        //= examNetSchoolService.detail(paramMap).getData();
        //return ResponseUtil.build(result);
        paramMap.put("aid", aid);
        HashMap resultMap = new HashMap();
        resultMap.put("aid", 12);
        resultMap.put("typeid", 189);
        resultMap.put("body", "<BR><BR>&nbsp;一、复试资格 \r\n<P><FONT face=\"Times New Roman\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 根据有关规定，按照录用人数与面试人数1：5的比例，结合新增职位的专业性要求，对报名申请调剂的考生依分数高低确定专业笔试、面试人员。2007年中办新增各招录职位最低分数线为：</FONT></P>\r\n<P><FONT face=\"Times New Roman\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 1、中共中央直属机关事务管理局：中直机关采购中心采购文件内审工作科员或副主任科员职位118.4分；中直机关采购中心计算机网络管理工作科员或副主任科员职位113.3分；中直机关采购中心信息管理工作科员或副主任科员职位111.9分。</FONT></P>\r\n<P><FONT face=\"Times New Roman\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 2、中央保密委员会办公室暨国家保密局：科技处科技管理工作副主任科员职位110.6分；法规及科技管理工作副主任科员或主任科员职位115分。</FONT></P>\r\n<P><FONT face=\"Times New Roman\">&nbsp;二、复试人员名单</FONT></P>\r\n<P>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 1、中共中央直属机关事务管理局复试人员名单：</P><FONT face=\"Times New Roman\"><FONT face=\"Times New Roman\">\r\n<DIV align=center>\r\n<TABLE cellSpacing=0 cellPadding=0 width=587 border=0>\r\n<TBODY>\r\n<TR>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>姓名</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>准考证号</DIV></TD>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>姓名</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>准考证号</DIV></TD>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>姓名</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>准考证号</DIV></TD>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>姓名</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>准考证号</DIV></TD></TR>\r\n<TR>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>于继峰</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>90218771215</DIV></TD>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>胡楠</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>13418311603</DIV></TD>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>张学峰</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>90218552416</DIV></TD>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>华青</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>13218450607</DIV></TD></TR>\r\n<TR>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>王培虎</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>90211930519</DIV></TD>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>董佳鑫</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>11618301925</DIV></TD>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>刘宇辉</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>11518731812</DIV></TD>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>黄丽华</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>94517910420</DIV></TD></TR>\r\n<TR>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>姚伟强</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>85118985721</DIV></TD>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>赵雷</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>13017871220</DIV></TD>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>周鹏</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>13513613122</DIV></TD>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>张方良</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>85118983005</DIV></TD></TR>\r\n<TR>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>成杰</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>14313241119</DIV></TD>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>郑艳丽</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>11017802518</DIV></TD>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>高菖</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>90218480906</DIV></TD>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center></DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>&nbsp;</DIV></TD></TR></TBODY></TABLE></DIV>\r\n<DIV align=left>\r\n<P><FONT face=\"Times New Roman\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 2、中央保密委员会办公室暨国家保密局复试人员名单：</FONT></P>\r\n<P><FONT face=\"Times New Roman\">\r\n<TABLE cellSpacing=0 cellPadding=0 width=587 border=0>\r\n<TBODY>\r\n<TR>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>姓名</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>准考证号</DIV></TD>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>姓名</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>准考证号</DIV></TD>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>姓名</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>准考证号</DIV></TD>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>姓名</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>准考证号</DIV></TD></TR>\r\n<TR>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>刘斌</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>11814413313</DIV></TD>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>江纯</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>11518190611</DIV></TD>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>周向阳</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>91218282527</DIV></TD>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>黎韦清</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>11517901407</DIV></TD></TR>\r\n<TR>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>范丽娜</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>95018450429</DIV></TD>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>舒文雯</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>94518441527</DIV></TD>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>任悦</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>96818975518</DIV></TD>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>李峰</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>11418150601</DIV></TD></TR>\r\n<TR>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>张洋</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>90217801128</DIV></TD>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>王兵</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>90217861116</DIV></TD>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>张昕</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>90212211421</DIV></TD>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>江");
        resultMap.put("redirecturl", "");
        resultMap.put("templet", "");
        resultMap.put("calendarid", 0);
        return resultMap;
    }

    /**
     * 用户点赞
     *
     * @param aid
     * @param type 1 点赞;-1取消点赞
     * @return
     */
    @PostMapping(value = "like/{aid}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object like(@PathVariable int aid,
                       @RequestParam(defaultValue = "1") int type) {

        HashMap paramMap = new HashMap();
        paramMap.put("id", aid);
        paramMap.put("type", type);
        NetSchoolResponse like = examNetSchoolService.like(paramMap);
        return ResponseUtil.build(like);
    }

    /**
     *备考精华分类列表
     *
     * @return
     */
    @GetMapping(value = "typeList")
    public Object typeList() {
        return ResponseUtil.build(examNetSchoolService.typeList());
    }


}
