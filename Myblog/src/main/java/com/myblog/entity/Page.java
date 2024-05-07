package com.myblog.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author lizx
 * @date 2020-01-30 - 12:23
 */
@Data
public class Page implements Serializable {
    private static final long serialVersionUID = -5448031440554277281L;
    private Integer id;

    private String key;

    private String title;

    private String content;

    private Date createTime;

    private Date updateTime;

    private Integer viewCount;

    private Integer commentCount;

    private Integer status;
}
