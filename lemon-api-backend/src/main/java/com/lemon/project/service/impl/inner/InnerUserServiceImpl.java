package com.lemon.project.service.impl.inner;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lemon.lemonapicommon.model.entity.User;
import com.lemon.lemonapicommon.service.InnerUserService;
import com.lemon.project.common.ErrorCode;
import com.lemon.project.exception.BusinessException;
import com.lemon.project.mapper.UserMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
@DubboService
public class InnerUserServiceImpl implements InnerUserService {

    @Resource
    private UserMapper userMapper;
    /**
     * 根据密钥获取内部信息用户
     * @param accessKey
     * @return
     */
    @Override
    public User getInvokeUser(String accessKey) {
        if (StringUtils.isAnyBlank(accessKey)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("accessKey",accessKey);
        return userMapper.selectOne(queryWrapper);
    }
}
