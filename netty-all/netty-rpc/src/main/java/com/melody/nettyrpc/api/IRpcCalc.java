package com.melody.nettyrpc.api;

/**
 * @author zqhuangc
 */
 public interface IRpcCalc {


    /**
     * 加
     * @param a
     * @param b
     * @return 和
     */
    int add(int a, int b);

    /**
     * 减
     * @param a
     * @param b
     * @return 差
     */
    int sub(int a, int b);

    /**
     * 乘
     * @param a
     * @param b
     * @return 积
     */
    int mult(int a, int b);


    /**
     * 除
     * @param a
     * @param b
     * @return 商
     */
    int div(int a, int b);
}
