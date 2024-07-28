package com.lemon.lemonapicommon.service;


import com.lemon.lemonapicommon.model.entity.User;

public interface InnerUserService {

    /**
     * 数据库中查找是否已分配给用户密钥
     * @param accessKey
     * @return
     */
    User getInvokeUser(String accessKey);
}
