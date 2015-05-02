package ru.gubber.query.filter;

import org.apache.log4j.Logger;
import org.hibernate.Query;

import java.util.Collections;
import java.util.List;

/**
 * Fake filter simulates one-parameter filter.
 * It simulates filter with one parameter that does not filter at all.
 * See ShowAllRequestsAction.getPagedList()
 * <p/>
 * XXX: It is some kind of bullshit
 * <p/>
 */


public class FakeFilter extends AbstractFilter {

    private final static Logger logger = Logger.getLogger(FakeFilter.class);

    public List getValues() {
        return Collections.singletonList(new Integer(1));
    }

    public void clear() {
    }

    public boolean isEmpty() {
        return false;
    }

    public int appendFilterCondition(StringBuilder sb, int filterCount) {
        sb.append("1 = 1");
        return 0;
    }

    public int fillParameters(Query query) {
        return fillParameters(query, 0);
    }

    public int fillParameters(Query query, int start) {
//        query.setInteger(start, 1);
        return 0;
    }

    public boolean accept(Object object) {
        return true;
    }

    public void setAlias(String alias) {
    }

    public Filter removeSubFilter(String name) {
        return null;
    }

    public Filter getSubFilter(String name) {
        return NullFilter.NULL_FILTER;
    }

    public List getSubFilters() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public void addValue(Object value) {
        logger.warn("Can't set value for ConstantFilter");
    }

}