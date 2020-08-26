package com.melody.orm.core;

/**
 * sql 排序
 * @author zqhuagnc
 */
public class Order {
    private boolean ascending;
    private String propertyName;

    /**
     * Constructor for Order.
     */
    protected Order(String propertyName, boolean ascending) {
        this.ascending = ascending;
        this.propertyName = propertyName;
    }

    @Override
    public String toString() {
        return propertyName + "" + (ascending ? "asc" : "desc");
    }

    /**
     * Ascending order
     *
     * @param propertyName
     * @return Order
     */
    public static Order asc(String propertyName){
        return new Order(propertyName,true);
    }

    /**
     * Descending order
     *
     * @param propertyName
     * @return Order
     */
    public static Order desc(String propertyName) {
        return new Order(propertyName, false);
    }
}
