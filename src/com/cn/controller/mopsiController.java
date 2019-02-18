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
        //创建模型跟视图，用于渲染页面。并且指定要返回的页面为home页面  
        ModelAndView mav = new ModelAndView("mopsi_jump");  
        return mav;  
    }  
}
