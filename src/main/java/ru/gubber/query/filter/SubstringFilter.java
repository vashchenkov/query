package ru.gubber.query.filter;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import ru.gubber.query.PagedList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Класс реализующий фильтр по подстроке
 */
public class SubstringFilter extends AbstractFilter implements SingleFilter {
    private static Logger logger = Logger.getLogger(SubstringFilter.class);
    protected String alias = PagedList.ALIAS;
    protected String value = null;
    protected Type type = StandardBasicTypes.STRING;
    protected String fieldName;

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Filter removeSubFilter(String name) {
        return null;
    }

    /**
     * @param fieldName - fieldName
     * @param value     - value
     */
    public SubstringFilter(String fieldName, String value) {
        this.fieldName = fieldName;
        this.value = value;
    }

    public List getValues() {
        ArrayList values = new ArrayList(1);
        values.add(value);
        return values;
    }

    public void clear() {
        value = null;
    }

    public boolean isEmpty() {
        return value == null || value.equals("");
    }

    public int appendFilterCondition(StringBuilder sb, int filterCount) {
//        StringBuilder sb = new StringBuilder();
        if (isEmpty())
            return 0;
        filterNames = new String[]{
                "filter_"+ (filterCount)
        };
        sb = sb.append(alias).append(".").append(fieldName).append(" LIKE :").append(filterNames[0]).append(' ');

        if (logger.getLevel() == Level.DEBUG)
            logger.debug("where condition = " + sb.toString());

        return 1;
    }

    public void fillParameters(Query query) {
        fillParameters(query, 0);
    }

    public int fillParameters(Query query, int start) {
        if (isEmpty()) return 0;
        String svalue = "%" + value.replaceAll("%", "\\\\%") + "%";

        //logger.debug("parameter " + start + ": svalue = " + svalue);
        query.setParameter("filter_"+(start++), svalue, type);
        return 1;
    }

    public boolean accept(Object object) {
        return true;
    }

    public Object getValue() {
        return value;
    }

    public Type getType() {
        return type;
    }

    public Filter getSubFilter(String name) {
        return NullFilter.NULL_FILTER;
    }

    public List getSubFilters() {
        return Collections.EMPTY_LIST;
    }

     @Override
    public void addValue(Object value) {
        this.value = (String) value;
    }

}