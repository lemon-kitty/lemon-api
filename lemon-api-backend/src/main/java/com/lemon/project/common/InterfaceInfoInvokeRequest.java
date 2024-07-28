package com.lemon.project.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class InterfaceInfoInvokeRequest implements Serializable {

    private Long id;

    /**
     * 用户请求参数
     */
    private String userRequestParams;

    private static final long serialVersionUID = 1L;
}
