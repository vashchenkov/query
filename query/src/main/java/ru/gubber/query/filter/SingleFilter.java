package ru.gubber.query.filter;

import org.hibernate.type.Type;

/**
 *
 * Для фильтров из одного-единственного значения
 */
public interface SingleFilter extends Filter{

    /**
     * @return Это самое одного-единственное значения
     */
    public Object getValue();

    /**
     * @return Его тип
     */
    public Type getType();
}