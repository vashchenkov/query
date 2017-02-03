package ru.gubber.query.filter;

import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Like фильтр
 */
public class LikeStringFilter extends SubstringFilter {
    private static Logger logger = LoggerFactory.getLogger(LikeStringFilter.class);

    public LikeStringFilter(String fieldName, String value) {
        super(fieldName, value);
    }

    public int fillParameters(Query query, int start) {
        if (isEmpty()) return 0;
        //logger.debug("parameter" + start + ": value = " + value);
        query.setParameter(FiltersConstans.ATTRIBUTE_PREFIX+(start++), value, type);
        return 1;
    }
}
