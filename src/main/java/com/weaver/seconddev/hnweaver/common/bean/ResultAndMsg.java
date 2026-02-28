package com.weaver.seconddev.hnweaver.common.bean;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@AllArgsConstructor
@Data
@ToString
public class ResultAndMsg {
    private boolean success;
    private String msg;
}
