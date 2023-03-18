package ru.gubber.query;

import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * Управляет выборкой объектов с возможностью постраничного разбиения, сортировки и фильтрации.
 */
public class PagedList {
    private static Logger logger = LoggerFactory.getLogger(PagedList.class);

    public static final String ALIAS = "foo";
    protected Class classToQuery;
    protected PageCounter counter = new PageCounter(DEFAULT_ITEMS_PER_PAGE);
    protected int currentPage = 0;
    protected List<Sorter> sorters = new ArrayList<>();
    protected ru.gubber.query.filter.Filter filter = NullFilter.NULL_FILTER;

    private static final int DEFAULT_ITEMS_PER_PAGE = 10;

    public PagedList(Class classToQuery) {
        this.classToQuery = classToQuery;
    }

    public PagedList(Class classToQuery, ru.gubber.query.filter.Filter filter) {
        this.classToQuery = classToQuery;
        this.filter = filter;
    }

    public Collection getItems(EntityManager session) throws HibernateException, JDBCException {
        try {
            Query query = getQuery(session);
            List items = new ArrayList();
            if (query != null) {
                items = query.getResultList();
            }
            updateTotalSize(session);
            return items;
        } catch (Throwable e) {
            logger.debug(e.getMessage(), e);
            throw e;
        }
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
        if (sorters.size() == 0)
            return NullSorter.NULL_SORTER;
        return sorters.get(0);
    }

    public void setSorter(Sorter sorter) {
        sorters.clear();
        sorters.add(sorter);
    }

    public void addSorter(Sorter sorter) {
        if (sorter instanceof NullSorter)
            return;
        sorters.add(sorter);
    }

    public List<Sorter> getSorters() {
        return new ArrayList<>(sorters);
    }

    public void setSorters(List<Sorter> sorters) {
        this.sorters = sorters;
    }

    protected StringBuilder getQueryFrom() {
        return new StringBuilder(" FROM ").append(classToQuery.getName()).append(" AS ").append(ALIAS);
    }


    /**
     * ну типа узнаёт общее число объектов, удовлетворяющих фильтру
     * и апдейтит текущую страницу (проверяет на выход за границу)
     */
    protected void updateTotalSize(EntityManager session) throws HibernateException, JDBCException {
        try {
            StringBuilder sb = new StringBuilder("SELECT COUNT(").append(ALIAS).append(")").append(getQueryFrom());
            if (!filter.isEmpty()) {
                sb = sb.append(" WHERE ");
                filter.appendFilterCondition(sb, 0);
            }
            Query query = session.createQuery(sb.toString());
            filter.fillParameters(query, 0);

            Iterator i = query.getResultList().iterator();
            if (i.hasNext()) {
                counter.setItemCount(((Long) i.next()).intValue());
            }
            if (currentPage > counter.getItemCount() / counter.getItemsPerPage()) {
                currentPage = counter.getItemCount() / counter.getItemsPerPage();
            }
        } catch (Throwable e) {
            logger.debug(e.getMessage(), e);
            throw e;
        }
    }

    protected Query getQuery(EntityManager session) throws HibernateException {
        final StringBuilder sb = new StringBuilder("SELECT ").append(ALIAS).append(getQueryFrom());
        if (!filter.isEmpty()) {
            sb.append(" WHERE ");
            filter.appendFilterCondition(sb, 0);
        }
        generateSorting(sb);

        Query query = session.createQuery(sb.toString());
        filter.fillParameters(query, 0);

        query.setFirstResult(currentPage * counter.getItemsPerPage());
        query.setMaxResults(counter.getItemsPerPage());
        return query;
    }

    void generateSorting(StringBuilder sb) {
        if (sorters.size() > 0) {
            sb.append(" order by");
            final int[] indx = new int[]{0};
            for (Sorter sorter : sorters) {
                if (indx[0]++ > 0) {
                    sb.append(",").append(sorter.addToQuery());
                } else {
                    sb.append(sorter.addToQuery());
                }
            }
        }
    }

    public void setFilter(ru.gubber.query.filter.Filter filter) {
        this.filter = filter;
    }
}
