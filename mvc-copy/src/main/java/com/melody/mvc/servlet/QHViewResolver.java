package com.melody.mvc.servlet;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zqhuangc
 */
public class QHViewResolver {
    private String viewName;
    private File file;

    protected QHViewResolver(String viewName,File file){
        this.viewName = viewName;
        this.file = file;
    }

    public String getViewName() {
        return viewName;
    }

    protected String parse(QHModelAndView mv) throws Exception{
        StringBuilder sb = new StringBuilder();
        RandomAccessFile raf = new RandomAccessFile(this.file,"r");

        try {
            //模板框架的语法是非常复杂，但是，原理是一样的
            //无非都是用正则表达式来处理字符串而已
            String line = null;
            while(null != (line = raf.readLine())){
                Matcher matcher = match(line);
                while(matcher.find()){
                    for (int i = 1; i <= matcher.groupCount(); i++) {
                        String paramName = matcher.group(i);
                        Object paramValue = mv.getModel().get(paramName);
                        if(null == paramValue){
                            continue;
                        }
                        line = line.replaceAll("@\\{" + paramName + "\\}",paramValue.toString());
                    }
                }
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            raf.close();
        }
    }

    private Matcher match(String line){
        Pattern pattern = Pattern.compile("@\\{(.+?)\\}",Pattern.CASE_INSENSITIVE);
        return pattern.matcher(line);
    }
}
