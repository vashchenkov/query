package ru.gubber.query;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.Query;
import org.hibernate.Session;
import ru.gubber.query.filter.NullFilter;
import ru.gubber.query.sorter.NullSorter;
import ru.gubber.query.sorter.Sorter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Управляет выборкой объектов с возможностью постраничного разбиения, сортировки и фильтрации.
 */
public class PagedList {
    private static Logger logger = Logger.getLogger(PagedList.class);

    public static final String ALIAS = "foo";
    protected Class classToQuery;
    protected PageCounter counter = new PageCounter(DEFAULT_ITEMS_PER_PAGE);
    protected int currentPage = 0;
    protected Sorter sorter = NullSorter.NULL_SORTER;
    protected ru.gubber.query.filter.Filter filter = NullFilter.NULL_FILTER;

    private static final int DEFAULT_ITEMS_PER_PAGE = 10;

    public PagedList(Class classToQuery) {
        this.classToQuery = classToQuery;
    }

    public PagedList(Class classToQuery, ru.gubber.query.filter.Filter filter) {
        this.classToQuery = classToQuery;
        this.filter = filter;
    }

    public Collection getItems(Session session) throws HibernateException, JDBCException {
        Query query = getQuery(session);
        List items = new ArrayList();
        if (query != null) {
            items = query.list();
        }
        updateTotalSize(session);
        return sorter.sort(items);
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

    public ru.gubber.query.filter.Filter getFilter() {
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
        return new StringBuilder(" FROM ").append( classToQuery.getName()).append(" AS ").append(ALIAS);
    }


    /**
     * ну типа узнаёт общее число объектов, удовлетворяющих фильтру
     * и апдейтит текущую страницу (проверяет на выход за границу)
     */
    protected void updateTotalSize(Session session) throws HibernateException, JDBCException {
        StringBuilder sb = new StringBuilder("SELECT COUNT(").append(ALIAS).append(")").append(getQueryFrom());
        if (!filter.isEmpty()) {
            sb = sb.append(" WHERE ");
            filter.appendFilterCondition(sb, 0);
        }
        Query query = session.createQuery(sb.toString());
        filter.fillParameters(query, 0);

        Iterator i = query.list().iterator();
        if (i.hasNext()) {
            counter.setItemCount(((Long) i.next()).intValue());
        }
        if (currentPage > counter.getItemCount() / counter.getItemsPerPage()) {
            currentPage = counter.getItemCount() / counter.getItemsPerPage();
        }
    }

    protected Query getQuery(Session session) throws HibernateException {
        StringBuilder sb = new StringBuilder("SELECT ").append(ALIAS).append(getQueryFrom());
        if (!filter.isEmpty()) {
            sb.append(" WHERE ");
            filter.appendFilterCondition(sb, 0);
        }
        sb.append(sorter.addToQuery());

        Query query = session.createQuery(sb.toString());
        filter.fillParameters(query, 0);

        query.setFirstResult(currentPage * counter.getItemsPerPage());
        query.setMaxResults(counter.getItemsPerPage());
        return query;
    }

    public void setFilter(ru.gubber.query.filter.Filter filter) {
        this.filter = filter;
    }
}
