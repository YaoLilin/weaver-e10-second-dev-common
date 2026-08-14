package com.weaver.seconddev.hnweaver.common.bean;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author yaolilin
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class ResultAndMsg {
    private boolean success;
    private String msg;
}
