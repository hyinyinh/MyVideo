package com.hy.tiktok.service.common;

import com.hy.tiktok.pojo.Users;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserHolder {
    private UserHolder(){}

    private static final ThreadLocal<Users> tl = new ThreadLocal<>();

    public static void saveUser(Users user){
        tl.set(user);
    }

    public static Users getUser(){
        return tl.get();
    }

    public static void removeUser(){
        tl.remove();
    }

}
