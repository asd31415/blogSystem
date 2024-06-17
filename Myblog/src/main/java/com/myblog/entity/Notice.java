package com.myblog.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Notice implements Serializable {
    private static final long serialVersionUID = 1234567890L;

    private Integer id;

    private String title;

    private String content;

    private Date createTime;

    private Date updateTime;

    private Integer status;

    private Integer order;
}
