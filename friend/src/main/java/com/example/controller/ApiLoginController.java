package com.example.controller;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.api.SnsServiceFactory;
import com.example.service.LoginService;

@Controller
//@CrossOrigin(origins = "http://192.168.0.201:3000", maxAge = 3600)
public class ApiLoginController {

   @Autowired
   private LoginService loginService;
   private String name = null;
   
   @GetMapping("/login")
   public String login(String name) {
      //name 은 naver, facebook, google
      this.name = name;
      String url = "redirect:" + loginService.getAuthorization(name);
      return url;
   }
   
   //authorization code 를 각 사이트에서 발급 받아옴(code)
   @GetMapping("/callback")
   public String callback(HttpSession session, String code, String state, String error, RedirectAttributes redirectAttributes) throws IOException, JSONException {
      session.setAttribute("oauth_state", SnsServiceFactory.STATE);
      if(error != null) { //에러발생시 오류페이지로 이동
         System.out.println("error: " + error);
      }
      loginService.getQuickSave(session, code, state, name);
      
      //userid 응답
      redirectAttributes.addFlashAttribute("userid", loginService.getQuickSave(session, code, state, name));
      return "redirect:http://192.168.0.201:3000";
   }
   
   //메일 인증
   @GetMapping("/email/validate")
   public String confirm(String email, String authkey) {
      String url = null;
      if(loginService.checkKey(authkey, email) == true) 
         url = "redirect:.."
               + "/confirm";
      else {
         System.out.println("인증 안됨 ㅋ");
      }
      return url;
   }
   
   @RequestMapping("confirm")
   public void confirn() {}
   
}