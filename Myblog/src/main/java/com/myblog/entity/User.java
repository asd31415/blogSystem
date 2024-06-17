package com.myblog.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Data
@Entity
@Table(name = "t_user")
public class User implements Serializable {
    private static final long serialVersionUID = -7813104768329168886L;

    @Id
    private Integer id;

    private String avatar;

    private String email;

    private String nickname;

    private String password;

    private Boolean type;

    private String username;

    private String url;

    @Column(name = "last_login_ip")
    private String lastLoginIp;

    @Column(name = "register_time")
    private Date registerTime;

    @Column(name = "update_time")
    private Date updateTime;

    @Column(name = "last_login_time")
    private Date lastLoginTime;

    private Boolean status;

    private String description;

    /**
     * 文章数量(不是数据库字段)
     */
    @Transient
    private Integer blogCount;


    public Boolean getType() {
        if(type == null){
            return false;
        }
        return type;
    }

    public Boolean getStatus() {
        if(status == null){
            return false;
        }
        return status;
    }
}
