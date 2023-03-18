package ru.gubber.query.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.Query;
import java.util.Collections;
import java.util.List;

/**
 * Класс реализующий фильтрацию списка по числовому полю
 */
public class SingleValueFilter extends AbstractFilter implements SingleFilter {
    private static Logger logger = LoggerFactory.getLogger(SingleValueFilter.class);
    private Object value;
    private String fieldName;

    public SingleValueFilter(String fieldName, Object value) {
        this.fieldName = fieldName;
        this.value = value;
    }


    public List getValues() {
        return Collections.singletonList(value);
    }

    public void clear() {
        value = null;
    }

    public boolean isEmpty() {
        // there is a bug here
        return value == null;
    }

    public int appendFilterCondition(StringBuilder sb, int attributesCount) {

        if (!isEmpty()) {
            filterNames = new String[]{
                    FiltersConstans.ATTRIBUTE_PREFIX+(attributesCount)
            };
            sb.append(getAlias()).append(".").append(fieldName).append(" = :").append(filterNames[0]).append(" ");
        } else {
            // there is also bug here
            filterNames = new String[]{};
            sb.append(getAlias()).append(".").append(fieldName).append(" is null");
        }
        if (logger.isDebugEnabled())
            logger.debug("where condition = " + sb.toString());
        return filterNames.length;
    }

    public int fillParameters(Query query, int start) {
        if (!isEmpty()) {
            //logger.debug("parameter " + start + ": value = " + value);
            query.setParameter(FiltersConstans.ATTRIBUTE_PREFIX+(start++), value);
            return 1;
        } else
            return 0;
    }

    public boolean accept(Object object) {
        return true;
    }

    public Filter getSubFilter(String name) {
        return NullFilter.NULL_FILTER;
    }

    public List getSubFilters() {
        return Collections.EMPTY_LIST;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public void addValue(Object value) {
        if (this.value == null) {
            this.value = value;
            changed = true;
        } else {
            logger.warn("Can't add more then one value to SingleValueFilter");
        }
    }

}