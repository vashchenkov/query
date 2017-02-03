package ru.gubber.query.filter;

import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

/**
 * Фильтр операцию значение которого надо обратить
 */
public class NegationFilter extends AbstractFilter {

    private final Logger logger = LoggerFactory.getLogger(NegationFilter.class);
    /**
     * Фильтр операцию значение которого надо обратить
     */
    private Filter filter;

    public NegationFilter(Filter filter) {
        this.filter = filter;
    }

    public List getValues() {
        return filter.getValues();
    }

    public void clear() {
        filter.clear();
    }

    public boolean isEmpty() {
        return filter.isEmpty();
    }

    public int appendFilterCondition(StringBuilder sb, int filterCount) {
        if (!filter.isEmpty())
            sb = sb.append("NOT (");
        filterCount = filterCount + filter.appendFilterCondition(sb, filterCount);
        sb.append(")");

        if (logger.isDebugEnabled())
            logger.debug("where condition = " + sb.toString());

        return filterCount;
    }

    public int fillParameters(Query query, int start) {
        return filter.fillParameters(query, start);
    }

    public boolean accept(Object object) {
        return true;
    }

    public void setAlias(String alias) {
    }

    public Filter removeSubFilter(String name) {
        return null;
    }

    /**
     * К подфильтру можно обратиться по имени "neg".
     */
    public Filter getSubFilter(String name) {
        if (name.equals("neg")) {
            return filter;
        } else {
            return NullFilter.NULL_FILTER;
        }
    }

    public List getSubFilters() {
        return Collections.singletonList(filter);
    }

    @Override
    public void addValue(Object value) {
        logger.warn("Can't set value for ConstantFilter");
    }
}
