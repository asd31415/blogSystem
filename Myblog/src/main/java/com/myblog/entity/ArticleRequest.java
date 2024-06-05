package com.myblog.entity;

import java.util.List;

public class ArticleRequest {
    private Integer blogId;

    private String description;
    private String title; // 新增title属性
    private String markdownContent;
    private boolean likeOption;
    private boolean commentOption;
    private boolean publishOption;
    private List<Integer> selectedTags;

    // 必须提供默认构造函数


    // Getter 和 Setter 方法
    public Integer getBlogId(){return blogId;}

    public void setBlogId(Integer id){this.blogId=id;}

    public String getDescription(){return description;}

    public void setDescription(String d){this.description=d;}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMarkdownContent() {
        return markdownContent;
    }

    public void setMarkdownContent(String markdownContent) {
        this.markdownContent = markdownContent;
    }

    public boolean isLikeOption() {
        return likeOption;
    }

    public void setLikeOption(boolean likeOption) {
        this.likeOption = likeOption;
    }

    public boolean isCommentOption() {
        return commentOption;
    }

    public void setCommentOption(boolean commentOption) {
        this.commentOption = commentOption;
    }

    public int isPublishOption() {
        if(publishOption) return 1;
        else return 0;
    }

    public void setPublishOption(boolean publishOption) {
        this.publishOption = publishOption;
    }

    public List<Integer> getSelectedTags() {
        return selectedTags;
    }

    public void setSelectedTags(List<Integer> selectedTags) {
        this.selectedTags = selectedTags;
    }
}
