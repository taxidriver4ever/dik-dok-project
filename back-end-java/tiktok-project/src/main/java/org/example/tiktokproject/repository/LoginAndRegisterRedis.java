package org.example.tiktokproject.repository;

import java.util.Set;

public interface LoginAndRegisterRedis {
    public void storeOTP(String otp, String userEmail);
    public boolean verifyOTP(String otp, String userEmail);
    public void storeUserNameWithUUID(String userName, String uuid);
    public boolean verifyUserNameWithUUID(String userName, String uuid);
    public void storeSubscribeUserName(String userName, String subscribeUserName);
    public boolean verifySubscribeUserName(String userName, String subscribeUserName);
    public boolean deleteSubscribeUserName(String userName, String subscribeUserName);
    public void storeUserTag(String userName, String tag);
    String getUserTag(String userName);
}
