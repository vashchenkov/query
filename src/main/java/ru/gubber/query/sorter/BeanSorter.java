package ru.gubber.query.sorter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Sorts beans by attributes of their attributes (any depth).
 *
 * I.e. if we have:
 * class ABar has attribute name (of String)
 * class AFoo has attribute bar (of ABar)
 *
 * List of AFoo's can be sorted by foo.bar.name
 */
class BeanSorter implements Sorter {
    private Class classToSort;
    private String query;
    private int order;

    // query must not include "foo."
    public BeanSorter(Class classToSort, String query, int order) {
        this.classToSort = classToSort;
        this.query = query;
        this.order = order;
    }

    public StringBuilder addToQuery() {
        return new StringBuilder("");
    }

    public List sort(List items) {
        List sortedList = new ArrayList(items);

        Comparator comparator;
        if (order == ORDER_ASCENDING) {
            comparator = new DeepComparator(classToSort, "xxx." + query);
        }
        else {
            comparator = new ReverseComparator(new DeepComparator(classToSort, "xxx." + query));
        }
        Collections.sort(sortedList, comparator);

        return sortedList;
    }

    public String getQuery() {
        return query;
    }

    public boolean isNull() {
        return false;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
