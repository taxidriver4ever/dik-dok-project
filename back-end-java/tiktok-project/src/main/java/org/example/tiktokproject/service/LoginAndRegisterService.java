package org.example.tiktokproject.service;

import org.example.tiktokproject.pojo.EmailWithCode;
import org.example.tiktokproject.pojo.NameWithUUID;

public interface LoginAndRegisterService {
    void SendOTP(String userEmail);
    NameWithUUID LoginOrRegister(EmailWithCode emailWithCode);
    NameWithUUID loginByPassword(String userEmail,String userPassword);
}
