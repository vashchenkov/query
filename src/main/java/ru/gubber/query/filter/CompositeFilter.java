package ru.gubber.query.filter;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.Query;

import java.util.*;

/**
 * Композитный фильтр, используется для объединения нескольких фильтров.
 */
public class CompositeFilter extends AbstractFilter {

    private final Logger logger = LogManager.getLogger(getClass());
    /**
     * Указывает логический оператор между всемя фильтрами входящими в данный множественный фильтр
     */
    protected String operator = AND_OPERATOR;
    /**
     * Список всех фильтров входящих в множественную фильтрацию
     */
    protected Map<String, Filter> filters = new HashMap();

    /**
     * Используется для определения операции логического умножения для множественного фильтра.
     */
    public static final String AND_OPERATOR = "AND";
    /**
     * Используется для определения операции логического сложения для множественного фильтра.
     */
    public static final String OR_OPERATOR = "OR";

    /**
     * Хранит список имён обязательных фильтров. Филтров, которые не будут удаляться при очистке фильтра.
     */
    protected Set obligatoriesFilters = new HashSet();

    /**
     * Добовляет фильтр во множественный фильтр
     *
     * @param name   имя фильтра в наборе фильтров
     * @param filter Фильтр, который добавляется в набор фильтров
     */
    public void addFilter(String name, Filter filter) {
        filters.put(name, filter);
        if ((filter.getAlias() == null) || (filter.getAlias().length() == 0))
            filter.setAlias(getAlias());
    }

    /**
     * Добавляет обязательный фильр
     *
     * @param name   имя фильтра в наборе фильтров
     * @param filter Фильтр, который добавляется в набор фильтров
     */
    public void addObligatoryFilter(String name, Filter filter) {
        obligatoriesFilters.add(name);
        addFilter(name, filter);
    }

    /**
     * Удаляет фильтр из подмножества фильтров данного множественного фильтра
     *
     * @param name имя фильтра в наборе подфильтров, который необходимо удалить
     * @return Ссылку на объект типа {@link ru.gubber.query.filter.Filter}, который удалён из списка подфильтров
     */
    public Filter removeSubFilter(String name) {
        return (Filter) filters.remove(name);
    }

    /**
     * Возвращает ссылку на фильтр из подмножества фильтров данного множественного фильтра
     *
     * @param name имя фильтра в наборе подфильтров, который необходимо получить
     * @return Ссылку на объект типа {@link ru.gubber.query.filter.Filter}, null - если фильтр с таким названием не найден
     */
    public Filter getSubFilter(String name) {
        if (filters.containsKey(name)) {
            return (Filter) filters.get(name);
        } else {
            return new NullFilter();
        }
    }

    /**
     * Возвращает неупорядоченный список подфильтров
     *
     * @return неупорядоченный список подфильтров
     */
    public List getSubFilters() {
        return new ArrayList(filters.values());
    }

    public List getValues() {
        return new ArrayList();
    }

    /**
     * Очищает фильтр. Только необязательные фильтры
     */
    public void clear() {
        Iterator i = filters.keySet().iterator();
        while (i.hasNext()) {
            String filterName = (String) i.next();
            if (!obligatoriesFilters.contains(filterName))
                ((Filter) filters.get(filterName)).clear();
        }
    }

    /**
     * Определяет пусто ли значение фильтров
     *
     * @return true, если все необязательные фильтры пусты, либо их нет, true - если хотябы один из необязательных фильтров не пуст
     */
    public boolean isEmpty() {
        boolean isEmpty = true;
        Iterator i = filters.keySet().iterator();
        while (i.hasNext()) {
            String filterName = (String) i.next();
            Filter filter = (Filter) filters.get(filterName);
            isEmpty &= filter.isEmpty();
        }

        return isEmpty;
    }

    /**
     * Генерирует значение фильтров
     *
     * @return значение фильтров
     * @param sb
     * @param filterCount
     */
    public int appendFilterCondition(StringBuilder sb, int filterCount) {
        if (isEmpty())  {
            // return "TRUE"; // Hibernate does not have true?
            sb.append("(1 = 1)");
            return 0;
        }
        sb.append("(");
        boolean first = true;
        Iterator i = filters.values().iterator();
        while (i.hasNext()) {
            Filter filter = (Filter) i.next();
            // XXX
            if (!first && !filter.isEmpty()) {
                sb.append(" ").append(operator).append(" ");
            }
            if (!filter.isEmpty()) {
//                sb = sb.append();
                filterCount = filterCount + filter.appendFilterCondition(sb, filterCount);
            }
            first = first && filter.isEmpty();
        }
        sb.append(")");
        if (logger.isDebugEnabled())
            logger.debug("where condition = " + sb.toString());
        return filterCount;
    }

    /**
     * @param query
     * @param start
     * @return
     */
    public int fillParameters(Query query, int start) {
        int pos = start;
        Iterator i = filters.values().iterator();
        while (i.hasNext()) {
            Filter filter = (Filter) i.next();
            pos += filter.fillParameters(query, pos);
        }
        return pos - start;
    }

    public boolean accept(Object object) {
        // XXX: now works only as and
        Iterator i = filters.values().iterator();
        while (i.hasNext()) {
            Filter filter = (Filter) i.next();
            if (!filter.accept(object))
                return false;
        }
        return true;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public void setAndOperator() {
        this.operator = AND_OPERATOR;
    }

    public void setOrOperator() {
        this.operator = OR_OPERATOR;
    }

    public Collection getSubFilterNames() {
        return filters.keySet();
    }

    public Set getObligatoriesFilters() {
        return obligatoriesFilters;
    }

    @Override
    public void addValue(Object value) {
        logger.warn("Can't set value for ConstantFilter");
    }
}