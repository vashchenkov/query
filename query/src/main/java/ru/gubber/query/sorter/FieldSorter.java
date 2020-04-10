package ru.gubber.query.sorter;

import ru.gubber.query.PagedList;

import java.util.List;

/**
 * Класс реализующий сортировщик по полям.
 */
public class FieldSorter implements Sorter {

    public enum NullOrder {
        NULL_FIRST("NULLS FIRST"), NULL_LASTS("NULLS LAST");
        private String sql;

        NullOrder(String sql) {
            this.sql = sql;
        }

    }

    private String attributeName;
    private int order = ORDER_ASCENDING;
    private NullOrder nullOrder = null;

    public FieldSorter(String attributeName, int order) {
        this(attributeName, order, null);
    }

    public FieldSorter(String attributeName, int order, NullOrder nullOrder) {
        this.attributeName = attributeName;
        this.order = order;
        this.nullOrder = nullOrder;
    }

    public StringBuilder addToQuery() {
        StringBuilder stringBuilder = new StringBuilder(" ").append(PagedList.ALIAS).append(".").append(attributeName).append(" ").append(getOrderString());

        if (nullOrder != null) {
            stringBuilder.append(" ").append(nullOrder.sql);
        }

        return stringBuilder;
    }

    public List sort(List items) {
        return items;
    }

    public String getQuery() {
        return attributeName;
    }

    public boolean isNull() {
        return false;
    }

    public void setQuery(String attributeName) {
        this.attributeName = attributeName;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    private String getOrderString() {
        return (order == ORDER_ASCENDING) ? "asc" : "desc";
    }
}
