package ru.gubber.query.sorter;

import java.util.List;

/**
 * Sorts out list of objects.
 *
 * @see ru.gubber.query.PagedList
 *
 * User: ikonv
 * Date: Jun 9, 2003
 * Time: 11:33:00 AM
 */
public interface Sorter {
    static final int ORDER_ASCENDING = 0;
    static final int ORDER_DESCENDING = 1;

    /**
     * Add stuff (actually 'order by .. (asc|desc)') to hibernate query.
     * One can leave query unmodified but sort items later (in sort)
     *
     * @return hibernate query with order keywords added
     */
    StringBuilder addToQuery();

    /**
     * Sort list items.
     * One can leave this method unmodified but add sort instructions to addToQuery
     *
     * @param items - List of items to sort
     * @return sorted List
    List sort(List items);
     */

    void setOrder(int order);
    int getOrder();

    /**
     * Set attribute name to order by.
     * Note: implementation of sorter may define traversal sort, i.e. sort by foo.bar.name
     *
     * @param attributeName - speaks for itself
     */
    void setQuery(String attributeName);

    /**
     * Get attribute name to order by.
     * Note: implementation of sorter may define traversal sort, i.e. sort by foo.bar.name
     *
     * @return attribute name (may be separated by dots)
     */
    String getQuery();

    /**
     * @return whether the sorter is null
     */
    boolean isNull();
}
