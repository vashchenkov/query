package ru.gubber.query.filter;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.type.Type;
import ru.gubber.query.PagedList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 */
public class ValueFilter extends AbstractFilter {
    private static Logger logger = Logger.getLogger(ValueFilter.class);
    protected String alias = PagedList.ALIAS;
    protected ArrayList values = new ArrayList();
    protected Type type;
    protected String fieldName;

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Filter removeSubFilter(String name) {
        return null;
    }

    public ValueFilter(String fieldName, Type type) {
        this.fieldName = fieldName;
        this.type = type;
    }

    public ValueFilter(String fieldName, Type type, Object value) {
        this.fieldName = fieldName;
        this.type = type;
        values.add(value);
    }

    public void addValue(Object value) {
        values.add(value);
    }

    public List getValues() {
        return values;
    }

    public void clear() {
        values.clear();
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public int appendFilterCondition(StringBuilder sb, int filterCount) {
        if (values.isEmpty())
        return 0;
        {
            filterNames = new String[values.size()];
            sb.append(alias).append(".").append(fieldName).append(" IN (");
            Iterator i = values.iterator();
            int indx =0;
            while (i.hasNext()) {
                i.next();
                filterNames[indx] = ":filter_"+(filterCount + indx);
                sb.append(filterNames[indx++]);
                if (i.hasNext()) {
                    sb.append(", ");
                }
            }
            sb.append(")");
        }
        if (logger.getLevel() == Level.DEBUG)
            logger.debug("where condition = " + sb.toString());

        return filterNames.length;
    }

    public int fillParameters(Query query, int start) {
        Iterator i = values.iterator();
        while (i.hasNext()) {
            Object value = i.next();
            query.setParameter("filter_"+(start++), value, type);
        }
        return values.size();
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

}
