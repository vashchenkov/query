package ru.gubber.query.filter;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.type.Type;
import ru.gubber.query.PagedList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Класс реализующий интервальную филтрацию поля
 */
public class IntervalFilter extends AbstractFilter implements SingleFilter {
    private static Logger logger = Logger.getLogger(IntervalFilter.class);
    /**
     * Алиас объекта на свойство которого накладывается фильтр
     */
    private String alias = PagedList.ALIAS;
    /**
     * Тип свойства
     */
    private Type type;
    /**
     * минимальное значение
     */
    private Comparable smaller = null;
    /**
     * максимальное значение
     */
    private Comparable bigger = null;
    /**
     * имя фильтруемого поля
     */
    private String fieldName;

    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * Ничего не делает.
     *
     * @param name
     * @return возвращает null
     */
    public Filter removeSubFilter(String name) {
        return null;
    }

    public IntervalFilter(String fieldName, Type type, Comparable smaller, Comparable bigger) {
        this.type = type;
        this.smaller = smaller;
        this.bigger = bigger;
        this.fieldName = fieldName;
    }

    public Filter getSubFilter(String name) {
        return NullFilter.NULL_FILTER;
    }

    public List getSubFilters() {
        return Collections.EMPTY_LIST;
    }


    /**
     * Возвращает список из минимального и максимально значения фильтра
     *
     * @return список из минимального и максимально значения фильтра
     */
    public List getValues() {
        ArrayList values = new ArrayList(2);
        values.add(smaller);
        values.add(bigger);
        return values;
    }

    /**
     * задаёт максимальному и минимальному значениям null
     */
    public void clear() {
        smaller = null;
        bigger = null;
        changed = false;
    }

    /**
     * Проверяет пусты ли значения фильтров
     *
     * @return true, если минимальное и максимальное значение фильтра равны null, false - в противном случае
     */
    public boolean isEmpty() {
        return smaller == null && bigger == null;
    }

    /**
     * Генерирует и возвращает строку для фильтрации запроса
     *
     * @param sb
     * @param filterCount
     * @return сгенерированную строку для фильтрации запроса
     */
    public int appendFilterCondition(StringBuilder sb, int filterCount) {
        if (isEmpty())
            return 0;

        if ((smaller != null) && (bigger != null)) {
            filterNames = new String[]{
                    "filter_"+(filterCount++),
                    "filter_"+(filterCount)
            };
            sb = sb.append(alias).append(".").append(fieldName).append(" between :").append(filterNames[0]).append(" and :").append(filterNames[1]).append(" ");
        }
        else if (smaller == null) {
            filterNames = new String[]{
                    "filter_"+(filterCount)
            };
            sb = sb.append(alias).append(".").append(fieldName).append(" <= :").append(filterNames[0]).append(" ");
        }
        else {
            filterNames = new String[]{
                    "filter_"+(filterCount)
            };
            sb = sb.append(alias).append(".").append(fieldName).append(" >= :").append(filterNames[0]).append(" ");
        }
        if (logger.getLevel() == Level.DEBUG)
            logger.debug("where condition = " + sb.toString());

        return filterNames.length;
    }

    public void fillParameters(Query query) {
        fillParameters(query, 0);
    }

    /**
     * Заполняет параметры в HQL-запросе
     *
     * @param query
     * @param start
     * @return
     */
    public int fillParameters(Query query, int start) {
        if (isEmpty())
            return 0;
        if ((smaller != null) && (bigger != null)) {
            //logger.debug("parameter " + start + ": value = " + smaller);
            query.setParameter("filter_"+(start++), smaller, type);
            //logger.debug("parameter " + start + ": value = " + smaller);
            query.setParameter("filter_"+(start), bigger, type);
            return 2;
        } else if (smaller == null)
            query.setParameter("filter_"+(start), bigger, type);
        else
            query.setParameter("filter_"+(start), smaller, type);

        return 1;
    }

    public boolean accept(Object object) {
        return true;
    }

    public Object getValue() {
        return null;
    }

    public Object getSmallerValue() {
        return smaller;
    }

    public Object getBiggerValue() {
        return bigger;
    }

    public Type getType() {
        return type;
    }

    @Override
    public void addValue(Object value) {
        if (!(value instanceof Comparable)) {
            logger.warn("Can't add value which not implemented Comparable");
        } else {
            if (smaller == null) {
                smaller = (Comparable) value;
                changed = true;
            } else if (bigger == null) {
                bigger = (Comparable) value;
                changed = true;
            } else {
                logger.warn("Can't more then 2 values to IntervalValue");
            }
        }
    }

}