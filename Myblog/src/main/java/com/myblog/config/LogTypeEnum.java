package com.myblog.config;

public enum LogTypeEnum {

    OPERATION("operation"),

    LOGIN("login"),

    REGISTER("register"),

    FORGET("forget"),

    ATTACHMENT("attachment");

    private String value;

    LogTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
