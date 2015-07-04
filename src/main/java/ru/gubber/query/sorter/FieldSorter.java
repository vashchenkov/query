package ru.gubber.query.sorter;

import ru.gubber.query.PagedList;

import java.util.List;

/**
 * Класс реализующий сортировщик по полям.
 */
public class FieldSorter implements Sorter {
    private String attributeName;
    private int order = ORDER_ASCENDING;

    public FieldSorter(String attributeName, int order) {
        this.attributeName = attributeName;
        this.order = order;
    }

    public StringBuilder addToQuery() {
        return new StringBuilder(" ").append(PagedList.ALIAS ).append(".").append(attributeName).append(" ").append(getOrderString());
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
