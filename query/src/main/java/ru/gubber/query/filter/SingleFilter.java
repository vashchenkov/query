package ru.gubber.query.filter;


/**
 *
 * Для фильтров из одного-единственного значения
 */
public interface SingleFilter extends Filter{

    /**
     * @return Это самое одного-единственное значения
     */
    public Object getValue();

}