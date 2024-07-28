package com.lemon.project.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lemon.lemonapicommon.model.entity.InterfaceInfo;
import com.lemon.lemonapicommon.service.InnerInterfaceInfoService;
import com.lemon.project.common.ErrorCode;
import com.lemon.project.exception.BusinessException;
import com.lemon.project.mapper.InterfaceInfoMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class InnerInterfaceInfoServiceImpl implements InnerInterfaceInfoService {
    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;
    /**
     * 根据URL和请求方法获取内部接口信息
     * @param url
     * @param method
     * @return
     */
    @Override
    public InterfaceInfo getInterfaceInfo(String url, String method) {
        if (StringUtils.isAnyBlank(url,method)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("url",url);
        queryWrapper.eq("method",method);

        return interfaceInfoMapper.selectOne(queryWrapper);
    }
}
