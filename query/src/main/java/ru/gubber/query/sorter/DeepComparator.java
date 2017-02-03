/*
 * Created on 25.02.2004
 * 
 * $Id: DeepComparator.java,v 1.2 2004/08/02 06:44:32
 */
package ru.gubber.query.sorter;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Comparator;

class DeepComparator implements Comparator {
    private static Logger logger = LoggerFactory.getLogger(DeepComparator.class);

    private Class classToSort;
    private String attributeName;
    private Method[] getters;

    public DeepComparator(Class classToSort, String attributeName) {
        this.classToSort = classToSort;
        this.attributeName = attributeName;
        try {
            findGetters();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Can't find method for " + attributeName, e);
        }
    }

    public int compare(Object lhs, Object rhs) {
        //logger.debug("attempt to compare " + lhs + " and " + rhs);
        if (classToSort.isInstance(lhs) && classToSort.isInstance(rhs)) {
            // we need to get deepest objects by query
            for (int i = 0; i < getters.length; i++) {
                try {
                    lhs = getters[i].invoke(lhs, null);
                    rhs = getters[i].invoke(rhs, null);

                    if (lhs == null) {
                        return rhs == null ? 0 : -1;
                    } else if (rhs == null) {
                        // assert lhs == null;
                        return 1;
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Can't invoke method " + getters[i].getName() +
                            "(" + e.getMessage() +")", e);
                }
            }
            Comparable comparable = (Comparable) lhs;

            if (lhs != null && rhs != null) {
                return comparable.compareTo(rhs);
            } else {
                return 1;
            }
        }
        return 1;
    }

    private void findGetters() throws NoSuchMethodException {
        String[] names = attributeName.split("\\.");
        if (names.length < 2) {
            getters = new Method[0];
        } else {
            getters = new Method[names.length - 1];
            Class cls = classToSort;
            for (int i = 0; i < names.length - 1; i++) {
                Method method = getMethod(cls, names[i + 1]);
                cls = method.getReturnType();
                getters[i] = method;
            }
        }
    }

    private Method getMethod(Class cls, String attributeName) throws NoSuchMethodException {
        String getterName = "get" + attributeName.substring(0, 1).toUpperCase() + attributeName.substring(1);
        Method method = cls.getDeclaredMethod(getterName, new Class[0]);

        return method;
    }
}
