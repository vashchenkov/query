package ru.gubber.query.filter;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.type.ObjectType;

/**
 * Created with IntelliJ IDEA.
 * User: Gubber
 * Date: 12.10.13
 * Time: 21:14
 */
public class EnumFilter extends ValueFilter{

    private final static Logger logger = Logger.getLogger(EnumFilter.class);

    private String filterName;

    public EnumFilter(String fieldName, String filterName) {
        super(fieldName, new ObjectType());

        this.filterName = filterName;
    }

    @Override
    public int appendFilterCondition(StringBuilder sb, int filterCount) {
        if (values.isEmpty()) {
            return 0;
        }
        filterNames = new String[]{
                "filter_"+ (filterCount)
        };
        sb.append(alias).append(".").append(fieldName).append("=").append(":").append(filterNames[0]).append(' ');
        if (logger.getLevel() == Level.DEBUG)
            logger.debug("where condition = " + sb.toString());

        return 1;
    }

    @Override
    public int fillParameters(Query query, int start) {
        if (!values.isEmpty()) {
            query.setParameter("filter_" + (start++), values.get(0));
            return 1;
        }
        return 0;
    }

    @Override
    public void addValue(Object value) {
        values.clear();
        values.add(value);
    }
}
