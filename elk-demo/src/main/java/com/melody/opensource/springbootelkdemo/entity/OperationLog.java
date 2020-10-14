package com.melody.opensource.springbootelkdemo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

import java.time.LocalDateTime;


/**
 * 操作日志
 *
 * @author zqhuangc
 */
@Getter
@Setter
@NoArgsConstructor
public class OperationLog implements Serializable {

    private static final long serialVersionUID = 6402397534960279238L;

    private Long id;

    /** 操作用户 */
    private String operator;

    /** 描述 */
    private String description;

    /** 方法名 */
    private String method;

    /** 参数 */
    private String params;

    /** 日志类型 */
    private String logType;

    /** 请求ip */
    private String requestIp;

    /** ip 所在地址 */
    private String address;

    /** 浏览器  */
    private String browser;

    /** 请求耗时 */
    private Long costTime;

    /** 异常详细  */
    private byte[] exceptionDetail;

    /** 创建日期 */
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime createTime;

    public OperationLog(String logType, Long costTime) {
        this.logType = logType;
        this.costTime = costTime;
    }
}
