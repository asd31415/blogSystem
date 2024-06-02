package com.myblog.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author lizx
 * @date 2020-01-30 - 12:05
 */
@Entity
@Data
@Table(name = "t_blog")
public class Blog implements Serializable {
    private static final long serialVersionUID = -212654738057672788L;

    @Id
    private Integer id;

    private Boolean appreciation;

    private Boolean commentabled;

    private String content;

    @Column(name = "createTime")
    private Date createTime;

    private String description;

    private String firstPicture;

    private String flag;

    private Integer published;

    private Boolean recommend;

    private Boolean shareStatement;

    private String title;

    private Date updateTime;

    private Integer views;

    @Column(name = "type_id")
    private Integer typeId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "comment_count")
    private Integer commentCount;

    @Column(name = "like_count")
    private Integer likeCount;

    /**
     * 文章类型
     * post  文章
     * page  页面
     */
    @Transient
    private Integer postType;

    @Transient
    private String url;

    @Transient
    private Type type;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Transient
    private List<Tag> tags = new ArrayList<>();

    @Transient
    private List<Integer> tagIdList;

/*    private String tagIds;*/


    public void init() {
        this.tagIdList = tagsToIds(this.getTags());
    }

    //1,2,3
    private List<Integer> tagsToIds(List<Tag> tags) {
        if (!tags.isEmpty()) {
            List<Integer> taglist = new ArrayList<Integer>();
            for (Tag tag : tags) {
                taglist.add(tag.getId());
            }
            return taglist;
        } else {
            return tagIdList;
        }
    }
}
