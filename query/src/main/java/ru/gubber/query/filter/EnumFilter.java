package ru.gubber.query.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.Query;

/**
 * Created with IntelliJ IDEA.
 * User: Gubber
 * Date: 12.10.13
 * Time: 21:14
 */
public class EnumFilter extends ValueFilter {

    private final static Logger logger = LoggerFactory.getLogger(EnumFilter.class);

    private String filterName;
    private boolean singleValue;

    public EnumFilter(String fieldName, String filterName) {
        this(fieldName, filterName, true);
    }

    public EnumFilter(String fieldName, String filterName, boolean singleValue) {
        super(fieldName, null);
        this.singleValue = singleValue;
        this.filterName = filterName;
    }

    @Override
    public int appendFilterCondition(StringBuilder sb, int attributesCount) {
        if (singleValue) {
            if (values.isEmpty()) {
                return 0;
            }
            filterNames = new String[]{
                    FiltersConstans.ATTRIBUTE_PREFIX + (attributesCount)
            };
            sb.append(alias).append(".").append(fieldName).append("=").append(":").append(filterNames[0]).append(' ');
            if (logger.isDebugEnabled())
                logger.debug("where condition = " + sb.toString());

            return 1;
        }
        return super.appendFilterCondition(sb, attributesCount);
    }

    @Override
    public int fillParameters(Query query, int start) {
        if (singleValue) {
            if (!values.isEmpty()) {
                query.setParameter(FiltersConstans.ATTRIBUTE_PREFIX + (start), values.get(0));
                return 1;
            }
            return 0;
        }
        return super.fillParameters(query, start);
    }

    @Override
    public void addValue(Object value) {
        if (singleValue)
            values.clear();
        values.add(value);
    }
}
