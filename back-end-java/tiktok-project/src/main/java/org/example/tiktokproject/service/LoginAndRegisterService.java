package org.example.tiktokproject.service;

import org.example.tiktokproject.pojo.ESUser;
import org.example.tiktokproject.pojo.EmailWithCode;
import org.example.tiktokproject.pojo.KeywordAndName;
import org.example.tiktokproject.pojo.NameWithUUID;

import java.util.List;

public interface LoginAndRegisterService {
    void SendOTP(String userEmail);
    NameWithUUID LoginOrRegister(EmailWithCode emailWithCode);
    NameWithUUID loginByPassword(String userEmail,String userPassword);
    List<ESUser> getUserByKeyword(KeywordAndName keyword);
    boolean subscribeUser(String userName,String subscribeUserName);
    boolean verifySubscribeUser(String userName,String subscribeUserName);
    boolean unsubscribeUser(String userName,String subscribeUserName);
}
