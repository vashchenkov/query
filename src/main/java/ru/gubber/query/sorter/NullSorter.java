package ru.gubber.query.sorter;

import java.util.List;

/**
 */
public class NullSorter implements Sorter {
    public static final Sorter NULL_SORTER = new NullSorter();
    
    private String query = "";
    private int order;


    public StringBuilder addToQuery() {
        return new StringBuilder("");
    }

    public List sort(List items) {
        return items;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }

    public String getQuery() {
        return query;
    }

    public boolean isNull() {
        return true;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
