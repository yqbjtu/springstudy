package com.yq.domain;

import lombok.Data;

/**
 * Simple to Introduction
 * className: Bonus
 *
 * @author EricYang
 * @version 2018/7/7 10:20
 */
@Data
public class Bonus {
    private String name;
    //奖金发放时间
    private long time;
    //奖金金额
    private int count;
}