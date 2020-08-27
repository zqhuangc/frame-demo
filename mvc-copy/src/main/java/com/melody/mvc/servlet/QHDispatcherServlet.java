package com.melody.mvc.servlet;


import com.melody.mvc.annocation.QHController;
import com.melody.mvc.annocation.QHRequestMapping;
import com.melody.mvc.annocation.QHRequestParam;
import com.melody.mvc.context.QHApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zqhuangc
 */
public class QHDispatcherServlet extends HttpServlet {

    private static final String LOCATION = "contextConfigLocation";

    private List<QHHandler> handlerMapping = new ArrayList<>();

    private Map<QHHandler,QHHandlerAdapter> handlerAdapters = new HashMap<>();

    private List<QHViewResolver> viewResolvers = new ArrayList<>();

    @Override
    public void init(ServletConfig config){
        //IOC容器必须要先初始化
        //假装容器已启动
        QHApplicationContext context = new QHApplicationContext(config.getInitParameter(LOCATION));
        //请求解析
        initMultipartResolver(context);
        //多语言、国际化
        initLocaleResolver(context);
        //主题View层的
        initThemeResolver(context);


        //解析url和Method的关联关系
        initHandlerMappings(context);
        //适配器
        initHandlerAdapters(context);

        //异常解析
        initHandlerExceptionResolvers(context);
        //视图转发（根据视图名字匹配到一个具体模板）
        initRequestToViewNameTranslator(context);

        //解析模板中的内容（拿到服务器传过来的数据，生成HTML）
        initViewResolvers(context);

        initFlashMapManager(context);

        System.out.println("QHSpring MVC is init.");
    }

    //请求解析
    private void initMultipartResolver(QHApplicationContext context){
        // TODO
    }
    //多语言、国际化
    private void initLocaleResolver(QHApplicationContext context){
        // TODO
    }
    //主题View层的
    private void initThemeResolver(QHApplicationContext context){
        // TODO
    }

    //解析url和Method的关联关系
    private void initHandlerMappings(QHApplicationContext context) {
        Map<String, Object> ioc = context.getAll();
        if (ioc.isEmpty()) {
            return;
        }

        //只要是由 @QHController 修饰的类，里面方法全部找出来
        //而且这个方法上应该要加了 @QHRequestMapping 注解，如果没加这个注解，这个方法是不能被外界来访问的
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {

            Class<?> clazz = entry.getValue().getClass();
            if(!clazz.isAnnotationPresent(QHController.class)){
                continue;
            }

            String url = "";

            if(clazz.isAnnotationPresent(QHRequestMapping.class)){
                QHRequestMapping requestMapping = clazz.getAnnotation(QHRequestMapping.class);
                url = requestMapping.value();
            }

            //扫描 @QHController 下面的所有的 public 方法
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (!method.isAnnotationPresent(QHRequestMapping.class)) {
                    continue;
                }
                QHRequestMapping requestMapping = method.getAnnotation(QHRequestMapping.class);

                String regex = (url + requestMapping.value()).replaceAll("/+","/");
                Pattern pattern = Pattern.compile(regex);
                handlerMapping.add(new QHHandler(pattern, entry.getValue(), method));
                System.out.println("Mapping: " + regex + " " +  method.toString());
            }
            //RequestMapping 会配置一个url，那么一个url就对应一个方法，并将这个关系保存到Map中
        }
    }

    //适配器
    //主要是用来动态匹配参数的
    private void initHandlerAdapters(QHApplicationContext context) {
        if(handlerMapping.isEmpty()){return;}
        //参数类型作为key，参数的索引号作为值
        Map<String,Integer> paramMapping = new HashMap<>();
        //只需要取出来具体的某个方法
        for(QHHandler handler : handlerMapping){
            //把这个方法上面所有的参数全部获取到
            Class<?>[] parameterTypes = handler.method.getParameterTypes();

            //有顺序，通过反射没法拿到参数名
            //匹配自定参数列表
            for(int i = 0; i < parameterTypes.length; i++) {
                Class<?> type = parameterTypes[i];

                if (type == HttpServletRequest.class ||
                        type == HttpServletResponse.class) {
                    paramMapping.put(type.getName(), i);
                }
            }

            //匹配Request和Response
            Annotation[][] pa = handler.method.getParameterAnnotations();
            for (int i = 0; i < pa.length; i ++){
                for(Annotation annotation:pa[i]){
                    if(annotation instanceof QHRequestParam){
                        String paramName = ((QHRequestParam) annotation).value();
                        if(!"".equals(paramName.trim())){
                            paramMapping.put(paramName,i);
                        }
                    }
                }
            }

            handlerAdapters.put(handler, new QHHandlerAdapter(paramMapping));

        }
    }

    //异常解析
    private void initHandlerExceptionResolvers(QHApplicationContext context){}

    //视图转发（根据视图名字匹配到一个具体模板）
    private void initRequestToViewNameTranslator(QHApplicationContext context){}

    private void initFlashMapManager(QHApplicationContext context) {
    }
    //解析模板中的内容（拿到服务器传过来的数据，生成HTML代码）
    private void initViewResolvers(QHApplicationContext context) {
        //模板一般是不会放到WebRoot下的，而是放在WEB-INF下，或者classes下
        //这样就避免了用户直接请求到模板
        //加载模板的个数，存储到缓存中
        //检查模板中的语法错误

        String property = context.getConfig().getProperty("templatePath");

        //归根到底就是一个文件，普通文件
        String rootPath = this.getClass().getClassLoader().getResource(property).getFile();

        File dir = new File(rootPath);

        for(File templateFile:dir.listFiles()){
            viewResolvers.add(new QHViewResolver(templateFile.getName(),templateFile));
        }
    }



    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write("500 Exception, Msg :" + Arrays.toString(e.getStackTrace()));
        }


    }

    private QHHandler getHandler(HttpServletRequest request){
        // 循环 handlerMapping 得到 handler
        if(handlerMapping.isEmpty()){return null;}

        String url = request.getRequestURI();
        String contextPath = request.getContextPath();
        url = url.replaceAll(contextPath, "").replaceAll("/+","/");

        for(QHHandler handler:handlerMapping){

            Matcher matcher = handler.pattern.matcher(url);
            if(!matcher.matches()){
                continue;
            }
            return handler;
        }
        return null;
    }

    private QHHandlerAdapter getHandlerAdapter(QHHandler handler) {
        if (handlerAdapters.isEmpty()) {
            return null;
        }

        return handlerAdapters.get(handler);
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) {
        try {
            //先取出来一个Handler，从HandlerMapping取
            QHHandler handler = getHandler(req);
            if(handler == null){
                resp.getWriter().write("404 Not Found");
                return;
            }

            //取适配器
            //由适配器去调用具体的方法
            QHHandlerAdapter handlerAdapter = getHandlerAdapter(handler);
            QHModelAndView mv = handlerAdapter.handle(req,resp,handler);

            //写一个模板框架
            //Veloctiy #
            //Freemark  #
            //JSP   ${name}

            //模板   @{name}
            applyDefaultViewName(resp, mv);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void applyDefaultViewName(HttpServletResponse resp, QHModelAndView mv)throws Exception{
        if(viewResolvers.isEmpty()){return;}
        if(null == mv){return;}

        for (QHViewResolver viewResolver:viewResolvers) {
            if(!mv.getView().equals(viewResolver.getViewName())){continue;}
            String result = viewResolver.parse(mv);
            if(result != null){
                resp.getWriter().write(result);
                break;
            }
        }


    }

}
