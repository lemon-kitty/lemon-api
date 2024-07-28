package com.lemon.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lemon.lemonapicommon.model.entity.InterfaceInfo;

/**
* @author tongmm
* @description 针对表【interface_info(接口信息)】的数据库操作Service
* @createDate 2024-07-12 16:20:06
*/
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);
}
