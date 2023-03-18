package ru.gubber.query.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gubber.query.PagedList;

import jakarta.persistence.Query;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Класс реализующий фильтр по подстроке
 */
public class SubstringFilter extends AbstractFilter implements SingleFilter {
    private static Logger logger = LoggerFactory.getLogger(SubstringFilter.class);
    protected String alias = PagedList.ALIAS;
    protected String value = null;
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

    public int appendFilterCondition(StringBuilder sb, int attributesCount) {
        if (isEmpty())
            return 0;
        filterNames = new String[]{
                FiltersConstans.ATTRIBUTE_PREFIX+ (attributesCount)
        };
        if (caseSensitive)
            sb = insertFieldInuery(sb);
        else {
            sb = sb.append("lower(").append(insertFieldInuery(sb)).append(')');
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

    private StringBuilder insertFieldInuery(StringBuilder sb) {
        return sb.append("cast (").append(alias).append(".").append(fieldName).append(" as string)");
    }

    public void fillParameters(Query query) {
        fillParameters(query, 0);
    }

    public int fillParameters(Query query, int start) {
        if (isEmpty()) return 0;
        String svalue = "%" + value.replaceAll("%", "\\\\%") + "%";

        //logger.debug("parameter " + start + ": svalue = " + svalue);
        query.setParameter(FiltersConstans.ATTRIBUTE_PREFIX + (start++), svalue);
        return 1;
    }

    public boolean accept(Object object) {
        return true;
    }

    public Object getValue() {
        return value;
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