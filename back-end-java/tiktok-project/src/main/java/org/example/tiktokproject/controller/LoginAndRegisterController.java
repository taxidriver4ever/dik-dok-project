package org.example.tiktokproject.controller;

import jakarta.annotation.Resource;
import org.example.tiktokproject.pojo.*;
import org.example.tiktokproject.service.LoginAndRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class LoginAndRegisterController {

    @Resource
    private LoginAndRegisterService loginAndRegisterService;

    @PostMapping("/OTP")
    public NormalResult SendOTP(@RequestBody NormalUser user) {
        String userEmail = user.getUserEmail();
        System.out.println(userEmail);
        loginAndRegisterService.SendOTP(userEmail);
        return NormalResult.success();
    }

    @PostMapping("/email-OTP")
    public NormalResult LoginOrRegister(@RequestBody EmailWithCode emailWithCode) {
        NameWithUUID nameWithUUID = loginAndRegisterService.LoginOrRegister(emailWithCode);
        if (nameWithUUID == null) return NormalResult.error("验证码错误");
        return NormalResult.success(nameWithUUID);
    }

    @PostMapping("/email-password")
    public NormalResult LoginByPassword(@RequestBody NormalUser user) {
        NameWithUUID nameWithUUID = loginAndRegisterService.loginByPassword(user.getUserEmail(), user.getUserPassword());
        if(nameWithUUID == null) return NormalResult.error("该用户无设置密码或不存在");
        return NormalResult.success();
    }

    @PostMapping("/data")
    public NormalResult selectUser(@RequestBody KeywordAndName keywordAndName) {
        List<ESUser> userByKeyword = loginAndRegisterService.getUserByKeyword(keywordAndName);
        if(userByKeyword == null) return NormalResult.error("");
        return NormalResult.success(userByKeyword);
    }

    @PostMapping("/subscription")
    public NormalResult subscribeUser(@RequestParam String userName,@RequestParam String subscribeUserName) {
        boolean b = loginAndRegisterService.subscribeUser(userName, subscribeUserName);
        if(b) return NormalResult.success();
        return NormalResult.error("subscribe failed");
    }

    @PostMapping("/verify-subscribe")
    public NormalResult verifySubscribeUser(@RequestParam String userName,@RequestParam String subscribeUserName) {
        boolean b = loginAndRegisterService.verifySubscribeUser(userName, subscribeUserName);
        if(b) return NormalResult.success(true);
        return NormalResult.error("Has subscribed");
    }

    @PostMapping("/cancel-subscription")
    public NormalResult cancelSubscription(@RequestParam String userName,@RequestParam String subscribeUserName) {
        boolean b = loginAndRegisterService.unsubscribeUser(userName, subscribeUserName);
        if(b) return NormalResult.success(true);
        return NormalResult.error("unsubscribe failed");
    }
}
