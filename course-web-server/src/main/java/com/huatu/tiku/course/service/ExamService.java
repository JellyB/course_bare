package com.huatu.tiku.course.service;

import com.huatu.common.SuccessMessage;
import com.huatu.tiku.course.bean.NetSchoolResponse;
import com.huatu.tiku.course.netschool.api.ExamNetSchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @创建人 lizhenjuan
 * @创建时间 2019/2/15
 * @描述 备考精华相关业务逻辑
 */
@Service
public class ExamService {

    @Autowired
    ExamNetSchoolService examNetSchoolService;

    public Object getArticleList(Map<String, Object> params) {
        NetSchoolResponse articleList = examNetSchoolService.getArticleList(params);
        System.out.println("结果是：{}" + articleList);
        if (null != articleList) {
            Object data = articleList.getData();
            return data;
        }
        return null;
    }

    public Object detail(int aid) {
        HashMap paramMap = new HashMap();
        paramMap.put("aid", aid);
        // NetSchoolResponse detail = examNetSchoolService.detail(paramMap);
        NetSchoolResponse detail = new NetSchoolResponse();
        HashMap resultMap = new HashMap();
        resultMap.put("aid", 12);
        resultMap.put("typeid", 189);
        resultMap.put("body", "<BR><BR>&nbsp;一、复试资格 \r\n<P><FONT face=\"Times New Roman\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 根据有关规定，按照录用人数与面试人数1：5的比例，结合新增职位的专业性要求，对报名申请调剂的考生依分数高低确定专业笔试、面试人员。2007年中办新增各招录职位最低分数线为：</FONT></P>\r\n<P><FONT face=\"Times New Roman\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 1、中共中央直属机关事务管理局：中直机关采购中心采购文件内审工作科员或副主任科员职位118.4分；中直机关采购中心计算机网络管理工作科员或副主任科员职位113.3分；中直机关采购中心信息管理工作科员或副主任科员职位111.9分。</FONT></P>\r\n<P><FONT face=\"Times New Roman\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 2、中央保密委员会办公室暨国家保密局：科技处科技管理工作副主任科员职位110.6分；法规及科技管理工作副主任科员或主任科员职位115分。</FONT></P>\r\n<P><FONT face=\"Times New Roman\">&nbsp;二、复试人员名单</FONT></P>\r\n<P>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 1、中共中央直属机关事务管理局复试人员名单：</P><FONT face=\"Times New Roman\"><FONT face=\"Times New Roman\">\r\n<DIV align=center>\r\n<TABLE cellSpacing=0 cellPadding=0 width=587 border=0>\r\n<TBODY>\r\n<TR>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>姓名</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>准考证号</DIV></TD>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>姓名</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>准考证号</DIV></TD>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>姓名</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>准考证号</DIV></TD>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>姓名</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>准考证号</DIV></TD></TR>\r\n<TR>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>于继峰</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>90218771215</DIV></TD>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>胡楠</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>13418311603</DIV></TD>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>张学峰</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>90218552416</DIV></TD>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>华青</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>13218450607</DIV></TD></TR>\r\n<TR>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>王培虎</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>90211930519</DIV></TD>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>董佳鑫</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>11618301925</DIV></TD>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>刘宇辉</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>11518731812</DIV></TD>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>黄丽华</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>94517910420</DIV></TD></TR>\r\n<TR>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>姚伟强</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>85118985721</DIV></TD>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>赵雷</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>13017871220</DIV></TD>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>周鹏</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>13513613122</DIV></TD>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>张方良</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>85118983005</DIV></TD></TR>\r\n<TR>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>成杰</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>14313241119</DIV></TD>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>郑艳丽</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>11017802518</DIV></TD>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>高菖</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>90218480906</DIV></TD>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center></DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>&nbsp;</DIV></TD></TR></TBODY></TABLE></DIV>\r\n<DIV align=left>\r\n<P><FONT face=\"Times New Roman\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 2、中央保密委员会办公室暨国家保密局复试人员名单：</FONT></P>\r\n<P><FONT face=\"Times New Roman\">\r\n<TABLE cellSpacing=0 cellPadding=0 width=587 border=0>\r\n<TBODY>\r\n<TR>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>姓名</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>准考证号</DIV></TD>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>姓名</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>准考证号</DIV></TD>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>姓名</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>准考证号</DIV></TD>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>姓名</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>准考证号</DIV></TD></TR>\r\n<TR>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>刘斌</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>11814413313</DIV></TD>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>江纯</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>11518190611</DIV></TD>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>周向阳</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>91218282527</DIV></TD>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>黎韦清</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>11517901407</DIV></TD></TR>\r\n<TR>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>范丽娜</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>95018450429</DIV></TD>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>舒文雯</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>94518441527</DIV></TD>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>任悦</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>96818975518</DIV></TD>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>李峰</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>11418150601</DIV></TD></TR>\r\n<TR>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>张洋</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>90217801128</DIV></TD>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>王兵</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>90217861116</DIV></TD>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>张昕</DIV></TD>\r\n<TD vAlign=bottom noWrap width=85>\r\n<DIV align=center>90212211421</DIV></TD>\r\n<TD vAlign=bottom noWrap width=61>\r\n<DIV align=center>江");
        resultMap.put("redirecturl", "");
        resultMap.put("templet", "");
        resultMap.put("calendarid", 0);
        return resultMap;
    }


    public Object like(HashMap paramMap) {
        NetSchoolResponse like = examNetSchoolService.like(paramMap);
        if (like.getMsg().equals("success")) {
            return SuccessMessage.create("操作成功!");
        }
        return SuccessMessage.create("操作失败！");

    }


}
