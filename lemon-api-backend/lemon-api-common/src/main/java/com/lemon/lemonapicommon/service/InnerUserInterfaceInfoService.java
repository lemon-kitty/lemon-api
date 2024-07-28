package com.lemon.lemonapicommon.service;

public interface InnerUserInterfaceInfoService {

    /**
     * 接口调用次数统计
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    boolean invokeCount(long interfaceInfoId,long userId);
}
