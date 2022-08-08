package ru.gubber.query.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gubber.query.PagedList;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Класс реализующий интервальную филтрацию поля
 */
public class IntervalFilter extends AbstractFilter implements SingleFilter {
    private static Logger logger = LoggerFactory.getLogger(IntervalFilter.class);
    /**
     * Алиас объекта на свойство которого накладывается фильтр
     */
    private String alias = PagedList.ALIAS;
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

    private boolean smallerValueSet = false;
    private boolean biggerValueSet = false;

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

    public IntervalFilter(String fieldName, Comparable smaller, Comparable bigger) {
        this.smaller = smaller;
        this.bigger = bigger;
        this.fieldName = fieldName;
        smallerValueSet = smaller !=null;
        biggerValueSet = bigger != null;
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
        smallerValueSet = false;
        biggerValueSet = false;
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
     * @param attributesCount
     * @return сгенерированную строку для фильтрации запроса
     */
    public int appendFilterCondition(StringBuilder sb, int attributesCount) {
        if (isEmpty())
            return 0;

        if ((smaller != null) && (bigger != null)) {
            filterNames = new String[]{
                    FiltersConstans.ATTRIBUTE_PREFIX +(attributesCount++),
                    FiltersConstans.ATTRIBUTE_PREFIX +(attributesCount)
            };
            sb = sb.append(alias).append(".").append(fieldName).append(" between :").append(filterNames[0]).append(" and :").append(filterNames[1]).append(" ");
        }
        else if (smaller == null) {
            filterNames = new String[]{
                    FiltersConstans.ATTRIBUTE_PREFIX +(attributesCount)
            };
            sb = sb.append(alias).append(".").append(fieldName).append(" <= :").append(filterNames[0]).append(" ");
        }
        else {
            filterNames = new String[]{
                    FiltersConstans.ATTRIBUTE_PREFIX +(attributesCount)
            };
            sb = sb.append(alias).append(".").append(fieldName).append(" >= :").append(filterNames[0]).append(" ");
        }
        if (logger.isDebugEnabled())
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
            query.setParameter(FiltersConstans.ATTRIBUTE_PREFIX +(start++), smaller);
            //logger.debug("parameter " + start + ": value = " + smaller);
            query.setParameter(FiltersConstans.ATTRIBUTE_PREFIX +(start), bigger);
            return 2;
        } else if (smaller == null)
            query.setParameter(FiltersConstans.ATTRIBUTE_PREFIX +(start), bigger);
        else
            query.setParameter(FiltersConstans.ATTRIBUTE_PREFIX +(start), smaller);

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

    @Override
    public void addValue(Object value) {
        if ((value != null) && !(value instanceof Comparable)) {
            logger.warn("Can't add value which not implemented Comparable");
        } else {
            if  (!smallerValueSet ) {
                smaller = (Comparable) value;
                smallerValueSet = true;
                changed = true;
            } else if (!biggerValueSet) {
                bigger = (Comparable) value;
                biggerValueSet = true;
                changed = true;
            } else {
                logger.warn("Can't more then 2 values to IntervalValue");
            }
        }
    }

}