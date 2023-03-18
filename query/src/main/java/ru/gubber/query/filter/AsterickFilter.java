package ru.gubber.query.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.Query;

/**
 * Класс для фильтрации списка по страковым значениям
 */
public class AsterickFilter extends SubstringFilter {
    private static Logger logger = LoggerFactory.getLogger(AsterickFilter.class);

    /**
     * Создаёт новый экземляр фильтра
     * @param fieldName имя свойства, которое необходимо фильтрова
     * @param value значение фильтра
     */
    public AsterickFilter(String fieldName, String value) {
        super(fieldName, value);
    }

    /**
     * Заменяет все * в исходной строке на %.
     * @param query {@link org.hibernate.Query}. Содержит HQL запрос на фильтрацию, в котором содержаться *
     * @param start номер параметра в запросе.
     * @return 0, если фильтр пуст. 1 если фильр не пуст
     */
    public int fillParameters(Query query, int start) {
        if (isEmpty()) return 0;
        String svalue = value.replaceAll("%", "\\\\%").replaceAll("\\*", "%");

        //logger.debug("parameter " + start + ": svalue = " + svalue);
        query.setParameter(FiltersConstans.ATTRIBUTE_PREFIX + (start++), svalue);
        return 1;
    }

}
