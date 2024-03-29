package ru.gubber.query.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.Query;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 */
public class NullFilter extends AbstractFilter {

    private final static Logger logger = LoggerFactory.getLogger(NullFilter.class);

    public static final Filter NULL_FILTER = new NullFilter();

    public List getValues() {
        return new ArrayList();
    }

    public void clear() {
    }

    public boolean isEmpty() {
        return true;
    }

    public int appendFilterCondition(StringBuilder sb, int attributesCount) {
        return 0;
    }

    public int fillParameters(Query query) {
        return 0;
    }

    public int fillParameters(Query query, int start) {
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
        return NULL_FILTER;
    }

    public List getSubFilters() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public void addValue(Object value) {
        logger.warn("Can't set value for ConstantFilter");
    }
}