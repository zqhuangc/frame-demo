package com.melody.mvc.context;



import com.melody.mvc.annocation.QHAutowired;
import com.melody.mvc.annocation.QHController;
import com.melody.mvc.annocation.QHService;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zqhuangc
 */
public class QHApplicationContext{

    private Map<String,Object> beanMap = new ConcurrentHashMap<>();

    private List<String> cacheClass = new ArrayList<>();

    private Properties config = new Properties();

    public QHApplicationContext(String location){
        InputStream is = null;
        try {
            // 定位资源文件
            is = this.getClass().getClassLoader().getResourceAsStream(location);
            // 加载资源文件
            config.load(is);
            String packageName = config.getProperty("scanPackage");
            // 注册 bean
            doRegister(packageName);
            // 实例化
            doCreateBean();
            // 注入属性
            populate();
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("IOC 容器已经初始化");

    }

    private void doRegister(String packageName){
        URL url = this.getClass().getClassLoader().
                getResource("/"+packageName.replaceAll("\\.","/"));

        File dir = new File(url.getFile());
        for (File file:dir.listFiles()) {
            if(file.isDirectory()){
                doRegister(packageName + "." + file.getName());
            }else{
                cacheClass.add(packageName + "." + file.getName().replace(".class","").trim());
            }

        }

    }

    private void doCreateBean(){
        if(cacheClass.size() == 0){ return; }
        try{
            for (String className:cacheClass) {
                Class<?> clazz = Class.forName(className);
                // 初始化指定类型
                if(clazz.isAnnotationPresent(QHController.class)){
                    // 类名首字母转为小写
                    String id = lowerCaseFirst(clazz.getSimpleName());
                    beanMap.put(id,clazz.newInstance());

                }else if(clazz.isAnnotationPresent(QHService.class)){

                    QHService service = clazz.getAnnotation(QHService.class);
                    //如果有自定义名，就优先用自定义名
                    String id = service.value();

                    if(!"".equals(id.trim())){
                        beanMap.put(id,clazz.newInstance());
                        continue;
                    }

                    //如果是空的，就用默认规则
                    //1、类名首字母小写
                    //如果这个类是接口
                    //2、可以根据类型类匹配

                    Class<?>[] interfaces = clazz.getInterfaces();
                    //如果这个类实现了接口，就用接口的类型作为id
                    for(Class<?> i : interfaces){
                        beanMap.put(i.getName(), clazz.newInstance());
                    }

                }else{
                    continue;
                }

            }

        }catch (Exception e){
            e.printStackTrace();

        }

    }

    /**
     * 首字母小写
     * @param str
     * @return
     */
    private String lowerCaseFirst(String str){
        char[] arr= str.toCharArray();
        arr[0] += 32;
        return String.valueOf(arr);

    }

    private void populate(){
        //首先要判断ioc容器中有没有东西
        if(beanMap.isEmpty()){
            return;
        }

        for (Map.Entry<String,Object> entry: beanMap.entrySet()) {
            //把所有的属性全部取出来，包括私有属性
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            for(Field field: fields){

                if(!field.isAnnotationPresent(QHAutowired.class)){
                    continue;
                }

                QHAutowired autowired = field.getAnnotation(QHAutowired.class);
                String id = autowired.value();

                //如果id为空，也就是说，自己没有设置，默认根据类型来注入
                if("".equals(id.trim())) {
                    id = field.getType().getName();
                }

                field.setAccessible(true);

                try{
                    field.set(entry.getValue(),beanMap.get(id));
                }catch (Exception e){
                    e.printStackTrace();
                    continue;
                }



            }

        }

    }

    public Map<String,Object> getAll(){
        return beanMap;
    }

    public Properties getConfig() {
        return config;
    }
}
