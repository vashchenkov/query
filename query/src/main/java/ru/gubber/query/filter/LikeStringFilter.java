package ru.gubber.query.filter;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.Query;

/**
 * Like фильтр
 */
public class LikeStringFilter extends SubstringFilter {
    private static Logger logger = LogManager.getLogger(LikeStringFilter.class);

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
