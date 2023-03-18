package ru.gubber.query.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.Query;

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
        query.setParameter(FiltersConstans.ATTRIBUTE_PREFIX+(start++), value);
        return 1;
    }
}
