package ru.gubber.query.filter;

import org.apache.log4j.LogManager;
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
    private static Logger logger = LogManager.getLogger(SubstringFilter.class);
    protected String alias = PagedList.ALIAS;
    protected String value = null;
    protected Type type = StandardBasicTypes.STRING;
    protected String fieldName;
    protected boolean caseSensitive = true;

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
        this(fieldName, value, true);
    }

    public SubstringFilter(String fieldName, String value, boolean caseSensitive) {
        this.value = value;
        this.fieldName = fieldName;
        this.caseSensitive = caseSensitive;
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
        if (isEmpty())
            return 0;
        filterNames = new String[]{
                FiltersConstans.ATTRIBUTE_PREFIX+ (filterCount)
        };
        if (caseSensitive)
            sb = sb.append(alias).append(".").append(fieldName);
        else {
            sb = sb.append("lower(").append(alias).append(".").append(fieldName).append(')');
        }
        sb = sb.append(" LIKE ");
        if (caseSensitive)
            sb = sb.append(":").append(filterNames[0]);
        else {
            sb = sb.append("lower(:").append(filterNames[0]).append(')');
        }
        sb = sb.append(' ');

        if (logger.isDebugEnabled())
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
        query.setParameter(FiltersConstans.ATTRIBUTE_PREFIX + (start++), svalue, type);
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