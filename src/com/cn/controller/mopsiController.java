package com.cn.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/mopsi")
public class mopsiController {

	@RequestMapping("/mopsi_jump")  
    public ModelAndView index(){  
		System.out.println("wotama");
        //����ģ�͸���ͼ��������Ⱦҳ�档����ָ��Ҫ���ص�ҳ��Ϊhomeҳ��  
        ModelAndView mav = new ModelAndView("mopsi_jump");  
        return mav;  
    }  
}
