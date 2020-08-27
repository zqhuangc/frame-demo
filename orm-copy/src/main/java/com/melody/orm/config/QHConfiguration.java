package com.melody.orm.config;

/**
 * @author zqhuangc
 */
public class QHConfiguration {

    private String scanPath;

    private final MapperRegistry mapperRegistry = new MapperRegistry();

    public MapperRegistry getMapperRegistry() {
        return mapperRegistry;
    }

    public QHConfiguration doScanPath(String scanPath){
        this.scanPath = scanPath;
        return this;
    }

    public void build(){
        if(null == scanPath || scanPath.length() < 0){
            throw new RuntimeException("scan path is required");
        }
    }

    public static void main(String[] args) {
        new QHConfiguration().doScanPath("com/melody/orm/domain/example").build();
    }


}
