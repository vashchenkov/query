package ru.gubber.query;

import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gubber.query.filter.Filter;
import ru.gubber.query.filter.NullFilter;
import ru.gubber.query.sorter.NullSorter;
import ru.gubber.query.sorter.Sorter;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Управляет выборкой объектов с возможностью постраничного разбиения, сортировки и фильтрации, основанный на дереве классов
 * {@link ClassTree}
 */
public class PagedListByTree extends PagedList {
    private static Logger logger = LoggerFactory.getLogger(PagedListByTree.class);

    /**
     * Алиас головного класса
     */
    public static final String ALIAS = "foo";
    /**
     * Головной класс для выборки
     */
    protected Class classToQuery;
    protected PageCounter counter = new PageCounter(DEFAULT_ITEMS_PER_PAGE);
    /**
     * Реализует древовидную структуру классов
     */
    protected ClassTree tree;
    protected int currentPage = 0;
    protected Sorter sorter = NullSorter.NULL_SORTER;
    protected Filter filter = NullFilter.NULL_FILTER;

    private static final int DEFAULT_ITEMS_PER_PAGE = 10;

    public PagedListByTree(Class classToQuery) {
        super(classToQuery, NullFilter.NULL_FILTER);
        tree = new ClassTree(classToQuery, ALIAS);
    }

    public PagedListByTree(Class classToQuery, Filter filter) {
        super(classToQuery, filter);
        tree = new ClassTree(classToQuery, ALIAS);
    }

    /**
     * Добавляет класс в древовидную структуру
     *
     * @param parent_alias    алиас родительского класа.
     * @param alias           алиас добовляемого класса
     * @param klass           добовляемый класс
     * @param parent_property свойство родительского класса, для доступа к списку сущностей данного класса
     */
    public void addClass(String parent_alias, String alias, Class klass, String parent_property) {
        tree.addChildrenKlass(parent_alias, alias, klass, parent_property);
    }

    /**
     * Добавляет класс под заглавный элемент
     *
     * @param alias           алиас добовляемого класса
     * @param klass           добовляемый класс
     * @param parent_property свойство родительского класса, для доступа к списку сущностей данного класса
     */
    public void addClass(String alias, Class klass, String parent_property) {
        addClass(ALIAS, alias, klass, parent_property);
    }

    /**
     * @see PagedList#getItems(EntityManager)
     */
    public Collection getItems(EntityManager session) throws HibernateException, JDBCException {
        Query query = getQuery(session);
        List items = new ArrayList();
        if (query != null) {
            items = query.getResultList();
            Iterator k = items.iterator();
            while (k.hasNext()) {
                Object o = k.next();
                if (!filter.accept(o))
                    k.remove();
            }
        }
        updateTotalSize(session);
        return items;
    }

    public int getPageCount() {
        return counter.getPageCount();
    }

    public void setPage(int pageNumber) {
        currentPage = pageNumber;
    }

    public int getPage() {
        return currentPage;
    }

    public void setItemsPerPage(int itemsPerPage) {
        counter.setItemsPerPage(itemsPerPage);
        if (currentPage != 0 && currentPage >= getPageCount()) {
            currentPage = getPageCount() - 1;
        }
    }

    public int getItemsPerPage() {
        return counter.getItemsPerPage();
    }

    public List getPageNumbers() {
        return counter.getPageNumbers(currentPage);
    }

    public int getItemCount() {
        return counter.getItemCount();
    }

    public Filter getFilter() {
        return filter;
    }

    public Class getClassToQuery() {
        return classToQuery;
    }

    public void setClassToQuery(Class classToQuery) {
        this.classToQuery = classToQuery;
    }

    public Sorter getSorter() {
        return sorter;
    }

    public void setSorter(Sorter sorter) {
        this.sorter = sorter;
    }

    protected StringBuilder getQueryFrom() {
        return new StringBuilder().append(" ").append(tree.getFullFromString());
    }

    /**
     * Строит WHERE часть HQL-запроса
     *
     * @return WHERE часть HQL-запроса
     */
    protected String getQueryWhere() {
        StringBuilder sb = new StringBuilder();
        String res = "";
        String result = "";
        filter.appendFilterCondition(sb, 0);
        res = sb.toString();
        if (res.trim().length() > 0) result = " WHERE " + res;
        return result;
    }

    /**
     * узнаёт общее число объектов, удовлетворяющих фильтру
     * и апдейтит текущую страницу (проверяет на выход за границу)
     */
    protected void updateTotalSize(EntityManager session) throws HibernateException, JDBCException {
        String queryString = "SELECT COUNT( DISTINCT " + tree.getAlias() + ")" + getQueryFrom() + getQueryWhere();

        //logger.debug("calculated query " + queryString);

        Query query = session.createQuery(queryString);
        filter.fillParameters(query, 0);

        Iterator i = query.getResultList().iterator();
        if (i.hasNext()) {
            counter.setItemCount(((Integer) i.next()).intValue());
        }
        if (currentPage > counter.getItemCount() / counter.getItemsPerPage()) {
            currentPage = counter.getItemCount() / counter.getItemsPerPage();
        }
    }

    /**
     * @see PagedList#getQuery(EntityManager)
     */
    protected Query getQuery(EntityManager session) throws HibernateException {
        StringBuilder sb = new StringBuilder("SELECT DISTINCT ").append(tree.getAlias()).append(getQueryFrom()).append(getQueryWhere());
        sb = sb.append(sorter.addToQuery());

        //logger.debug("calculated query " + queryString);

        Query query = session.createQuery(sb.toString());
        filter.fillParameters(query, 0);

        query.setFirstResult(currentPage * counter.getItemsPerPage());
        query.setMaxResults(counter.getItemsPerPage());
        return query;
    }

    /**
     * @see PagedList#setFilter(Filter)
     */
    public void setFilter(Filter filter) {
        this.filter = filter;
    }
}