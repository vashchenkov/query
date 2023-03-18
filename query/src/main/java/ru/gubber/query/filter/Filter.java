/*
 * $Id: Filter.java,v 1.1.1.1 2006-11-12 10:44:12 Gubber Exp $
 */

package ru.gubber.query.filter;

import jakarta.persistence.Query;
import java.util.List;

/**
 * Интерфейс фильтра. См. паттерн Composite.
 *
 */
public interface Filter {

    /**
     * Задать алиас имени таблицы в запросе
     */
    void setAlias(String alias);

    String getAlias();

    /**
     * Удалить фильтр по имени.
     *
     * @param name имя фильтра
     * @return удаленный Filter или null, если нет такого имени.
     */
    Filter removeSubFilter(String name);

    /**
     * Получить фильтр по имени.
     *
     * @param name имя фильтра
     * @return Filter или NullFilter, если нет такого имени.
     *
     * // Странная операция для произвольного фильтра... // Yozh
     */
    Filter getSubFilter(String name);

    /**
     * Фильтры, посредством которых реализован данный фильтр.
     *
     * @return List of Filter
     */
    List getSubFilters();

    /**
     * Получить список значений фильтра.
     * <b>NB:</b> выдаётся список значений самого фильтра, без подфильтров.
     *
     * @return List of Object (filter values)
     */
    List getValues();

    /**
     * Почистить (рекурсивно) значения фильтров.
     */
    void clear();

    /**
     * Содержит ли фильтр условие или не фильтрует (рекурсивно).
     *
     * @return true, если фильтр содержит условие
     */
    boolean isEmpty();

    /**
     * Сгенерировать WHERE-условие (без слова 'where')
     *
     * @return String that can be used in WHERE
     * @param stringBuilder
     * @param attributesCount
     */
    int appendFilterCondition(StringBuilder stringBuilder, int attributesCount);

    /**
     * Заполнить параметры запроса, начиная с некоторого параметра
     *
     * @param query hibernate query
     * @param start - first parameter in query to start from
     * @return count of used parameters
     */
    int fillParameters(Query query, int start);

    // XXX: document
    boolean accept(Object object);

    /**
     * Where this filter gets removed or not on filter clean.
     */
    boolean isMandatory();

    void addValue(Object value);
}