package ru.gubber.query.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.Query;
import java.util.*;

/**
 * Композитный фильтр, используется для объединения нескольких фильтров.
 */
public class CompositeFilter extends AbstractFilter {

    private final Logger logger = LoggerFactory.getLogger(getClass());
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
     * @return Ссылку на объект типа {@link Filter}, который удалён из списка подфильтров
     */
    public Filter removeSubFilter(String name) {
        return (Filter) filters.remove(name);
    }

    /**
     * Возвращает ссылку на фильтр из подмножества фильтров данного множественного фильтра
     *
     * @param name имя фильтра в наборе подфильтров, который необходимо получить
     * @return Ссылку на объект типа {@link Filter}, null - если фильтр с таким названием не найден
     */
    public Filter getSubFilter(String name) {
        if (name == null)
            return new NullFilter();
        String[] path = name.split("/");
        return findFilterByPath(path);
    }

    private Filter findFilterByPath(String[] path) {
        if (path.length == 1) {
            String name = path[0];
            if (filters.containsKey(name)) {
                return filters.get(name);
            } else {
                return new NullFilter();
            }
        }
        String name = path[0];
        Filter nextRootFilter = filters.get(name);
        if (!(nextRootFilter instanceof CompositeFilter))
            return new NullFilter();
        String [] subPath = new String[path.length -1];
        System.arraycopy(path, 1, subPath, 0, path.length - 1);
        return ((CompositeFilter)nextRootFilter).findFilterByPath(subPath);
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
     * @param attributesCount
     */
    public int appendFilterCondition(StringBuilder sb, int attributesCount) {
        if (isEmpty())  {
            // return "TRUE"; // Hibernate does not have true?
            sb.append("(1 = 1)");
            return 0;
        }
        sb.append("(");
        boolean first = true;
        int pos = attributesCount;
        Iterator i = filters.values().iterator();
        while (i.hasNext()) {
            Filter filter = (Filter) i.next();
            // XXX
            if (!first && !filter.isEmpty()) {
                sb.append(" ").append(operator).append(" ");
            }
            if (!filter.isEmpty()) {
                pos+= filter.appendFilterCondition(sb, pos);
            }
            first = first && filter.isEmpty();
        }
        sb.append(")");
        if (logger.isDebugEnabled())
            logger.debug("where condition = " + sb);
        return pos - attributesCount;
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