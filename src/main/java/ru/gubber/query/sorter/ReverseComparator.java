/*
 * Created on 25.02.2004
 * 
 * $Id: ReverseComparator.java,v 1.1.1.1 2006-11-12 10:44:12 Gubber Exp $
 */
package ru.gubber.query.sorter;

import java.util.Comparator;

class ReverseComparator implements Comparator {
    private Comparator comparator;

    public ReverseComparator(Comparator comparator) {
        this.comparator = comparator;
    }

    public int compare(Object lhs, Object rhs) {
        return - comparator.compare(lhs, rhs);
    }
}

