package ru.gubber.query.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gubber.query.PagedList;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 */
public class ValueFilter extends AbstractFilter {
    private static Logger logger = LoggerFactory.getLogger(ValueFilter.class);
    protected String alias = PagedList.ALIAS;
    protected ArrayList values = new ArrayList();
    protected String fieldName;

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Filter removeSubFilter(String name) {
        return null;
    }

    public ValueFilter(String fieldName) {
        this.fieldName = fieldName;
    }

    public ValueFilter(String fieldName, Object value) {
        this.fieldName = fieldName;
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
            sb.append('(').append(alias).append(".").append(fieldName).append(" IN (");
            Iterator i = values.iterator();
            int indx =0;
            while (i.hasNext()) {
                i.next();
                filterNames[indx] = ":"+FiltersConstans.ATTRIBUTE_PREFIX+(filterCount + indx);
                sb.append(filterNames[indx++]);
                if (i.hasNext()) {
                    sb.append(", ");
                }
            }
            sb.append(")");
        }
        sb.append(")");
        if (logger.isDebugEnabled())
            logger.debug("where condition = " + sb.toString());

        return filterNames.length;
    }

    public int fillParameters(Query query, int start) {
        Iterator i = values.iterator();
        while (i.hasNext()) {
            Object value = i.next();
            if (logger.isDebugEnabled()) {
                logger.debug("set " + FiltersConstans.ATTRIBUTE_PREFIX + (start) + "=" + value + "." );
            }
            query.setParameter(FiltersConstans.ATTRIBUTE_PREFIX + (start++), value);
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
