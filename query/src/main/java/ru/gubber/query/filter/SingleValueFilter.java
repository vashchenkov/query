package ru.gubber.query.filter;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;

import java.util.Collections;
import java.util.List;

/**
 * Класс реализующий фильтрацию списка по числовому полю
 */
public class SingleValueFilter extends AbstractFilter implements SingleFilter {
    private static Logger logger = LogManager.getLogger(SingleValueFilter.class);
    private Object value = null;
    private Type type;
    private String fieldName;

    public SingleValueFilter(String fieldName, Type type, Object value) {
        if (value == null)
            logger.warn("ne nado null v value sovat");
        this.fieldName = fieldName;
        this.type = type;
        this.value = value;
    }

    /**
     * Create filter with given fieldName, value and type={@link org.hibernate.type.StandardBasicTypes.INTEGER}.
     */
    public SingleValueFilter(String fieldName, int value) {
        this(fieldName, StandardBasicTypes.INTEGER, new Integer(value));
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

    public int appendFilterCondition(StringBuilder sb, int filterCount) {

        if (!isEmpty()) {
            filterNames = new String[]{
                    FiltersConstans.ATTRIBUTE_PREFIX+(filterCount)
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
            query.setParameter(FiltersConstans.ATTRIBUTE_PREFIX+(start++), value, type);
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

    public Type getType() {
        return type;
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