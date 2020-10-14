package com.melody.opensource.springbootelkdemo.aspect;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import com.melody.opensource.springbootelkdemo.annotation.AopLog;
import com.melody.opensource.springbootelkdemo.entity.OperationLog;
import com.melody.opensource.springbootelkdemo.utils.ExceptionUtil;
import com.melody.opensource.springbootelkdemo.utils.RequestUtil;
import com.melody.opensource.springbootelkdemo.utils.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * 业务日志切面
 *
 * @author zqhuangc
 */
@Component
@Aspect
@Slf4j
public class LogAspect {

    private final ObjectMapper objectMapper = new ObjectMapper();

    ThreadLocal<Long> currentTime = new ThreadLocal<>();

    /**
     * 配置切入点
     */
    @Pointcut("@annotation(com.melody.opensource.springbootelkdemo.annotation.AopLog)")
    public void logPointcut() { }

    /**
     * 配置环绕通知
     *
     * @param joinPoint join point for advice
     */
    @Around("logPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result;
        currentTime.set(System.currentTimeMillis());
        result = joinPoint.proceed();
        OperationLog operationLog = new OperationLog("INFO", (System.currentTimeMillis() - currentTime.get()));
        currentTime.remove();
        logMethodInfo(joinPoint, operationLog);
        return result;
    }

    /**
     * 配置异常通知
     *
     * @param joinPoint join point for advice
     * @param e exception
     */
    @AfterThrowing(pointcut = "logPointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        OperationLog operationLog = new OperationLog("ERROR", (System.currentTimeMillis() - currentTime.get()));
        currentTime.remove();
        operationLog.setExceptionDetail(ExceptionUtil.getStackTrace(e).getBytes());
        logMethodInfo((ProceedingJoinPoint) joinPoint, operationLog);
    }


    public void logMethodInfo(ProceedingJoinPoint joinPoint, OperationLog operationLog){

        currentTime.set(System.currentTimeMillis());

        HttpServletRequest request = RequestUtil.getHttpServletRequest();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        AopLog aopLog = method.getAnnotation(AopLog.class);
        if(aopLog == null || !aopLog.enable()){
            return;
        }
        // 方法描述
        if (operationLog != null) {
            operationLog.setDescription(aopLog.value());
        }

        // 方法路径
        String methodName = joinPoint.getTarget().getClass().getName() + "." + signature.getName() + "()";
        operationLog.setMethod(StrUtil.emptyIfNull(methodName));

        StringBuilder params = new StringBuilder("{");
        // 方法参数值
        List<Object> argValues = new ArrayList<>(Arrays.asList(joinPoint.getArgs()));
        // 参数名称
        for (Object argValue : argValues) {
            params.append(argValue).append(" ");
        }
        operationLog.setParams(params.toString() + " }");

        operationLog.setRequestIp(IpUtil.getIpAddr(request));
        // 通过 ip 查询 地址
        //bizLog.setAddress(...);
        operationLog.setBrowser(RequestUtil.getBrowser(request));

        // 操作时间
        operationLog.setCreateTime(LocalDateTime.now());


        // 操作用户需根据具体项目做调整
        String username = "";
        if ("login".equals(signature.getName())) {
            try {
                username =((ObjectNode) argValues.get(0)).get("username").asText();
            } catch (Exception e) {
               log.error(e.getMessage(), e);
            }
        }else{
            // 从权限管理框架获取
            username = "default_role";
        }
        operationLog.setOperator(username);

        // 根据需要可选择存储在数据库
        // json 序列化
        try {
            log.info(objectMapper.writeValueAsString(operationLog));
        } catch (JsonProcessingException e) {
            log.error(ExceptionUtil.getStackTrace(e));
        }

    }
}
