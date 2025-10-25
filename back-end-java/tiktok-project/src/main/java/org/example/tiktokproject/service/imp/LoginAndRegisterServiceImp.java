package org.example.tiktokproject.service.imp;

import jakarta.annotation.Resource;
import org.example.tiktokproject.AOP.MyLog;
import org.example.tiktokproject.mapper.LoginAndRegisterMapper;
import org.example.tiktokproject.pojo.*;
import org.example.tiktokproject.repository.LoginAndRegisterRedis;
import org.example.tiktokproject.repository.UserESRepository;
import org.example.tiktokproject.service.LoginAndRegisterService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

@Service
public class LoginAndRegisterServiceImp implements LoginAndRegisterService {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private LoginAndRegisterRedis loginAndRegisterRedis;

    @Resource
    private LoginAndRegisterMapper loginAndRegisterMapper;

    @Resource
    private UserESRepository userESRepository;

    @Override
    public void SendOTP(String userEmail) {
        rabbitTemplate.convertAndSend("direct.SendOTP.exchange", "SendOTP", userEmail);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public NameWithUUID LoginOrRegister(EmailWithCode emailWithCode) {
        if(!loginAndRegisterRedis.verifyOTP(emailWithCode.getCode(),emailWithCode.getUserEmail())) return null;
        String userName = loginAndRegisterMapper.selectUserNameByEmail(emailWithCode.getUserEmail());

        NameWithUUID nameWithUUID = new NameWithUUID();
        String stringUUID = UUID.randomUUID().toString();
        if(userName==null) {
            String stringName = UUID.randomUUID().toString();
            loginAndRegisterMapper.insertUserByEmailAndName(emailWithCode.getUserEmail(), stringName);
            nameWithUUID.setName(stringName);

            ESUser esUser = new ESUser();
            esUser.setUserEmail(emailWithCode.getUserEmail());
            esUser.setUserName(stringName);
            esUser.setCreatedTimeMilli(System.currentTimeMillis());
            esUser.setUpdatedTimeMilli(System.currentTimeMillis());
            userESRepository.save(esUser);
        }
        else nameWithUUID.setName(userName);
        nameWithUUID.setUuid(stringUUID);

        loginAndRegisterRedis.storeUserNameWithUUID(nameWithUUID.getName(), nameWithUUID.getUuid());
        return nameWithUUID;
    }

    @Override
    public NameWithUUID loginByPassword(String userEmail, String userPassword) {
        String userName = loginAndRegisterMapper.selectUserNameByUserEmailAndPassword(userEmail, userPassword);
        System.out.println(userName);
        if(userName==null) return null;
        else {
            NameWithUUID nameWithUUID = new NameWithUUID();
            nameWithUUID.setName(userName);
            nameWithUUID.setUuid(UUID.randomUUID().toString());
            loginAndRegisterRedis.storeUserNameWithUUID(nameWithUUID.getName(), nameWithUUID.getUuid());
            return nameWithUUID;
        }
    }

    @Override
    public List<ESUser> getUserByKeyword(KeywordAndName keyword) {
        List<ESUser> users = userESRepository.findByUserName(keyword.getKeyword());
        if(users.isEmpty()) return null;
        String name = keyword.getName();
        if(name==null) return null;
        for (ESUser user : users)
            user.setStatus(loginAndRegisterRedis.verifySubscribeUserName(name, user.getUserName()));
        return users;
    }

    @MyLog
    @Override
    public boolean subscribeUser(String userName, String subscribeUserName) {
        if(loginAndRegisterRedis.verifySubscribeUserName(userName, subscribeUserName)) return false;
        loginAndRegisterRedis.storeSubscribeUserName(userName, subscribeUserName);
        return true;
    }

    @MyLog
    @Override
    public boolean verifySubscribeUser(String userName, String subscribeUserName) {
        return loginAndRegisterRedis.verifySubscribeUserName(userName, subscribeUserName);
    }

    @MyLog
    @Override
    public boolean unsubscribeUser(String userName, String subscribeUserName) {
        return loginAndRegisterRedis.deleteSubscribeUserName(userName, subscribeUserName);
    }

}
