package com.lemon.lemonapicommon.service;

import com.lemon.lemonapicommon.model.entity.InterfaceInfo;

public interface InnerInterfaceInfoService {

    /**
     * 从数据库中查找模拟接口是否存在
     * @param path
     * @param method
     * @return
     */
    InterfaceInfo getInterfaceInfo(String path, String method);
}
