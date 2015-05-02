package ru.gubber.query.filter;

import ru.gubber.query.PagedList;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Gubber
 * Date: 15.03.14
 * Time: 11:05
 */
public class FilterManager {
    private PagedList list;
    private List<String> obligatoriesFilters = null;
    private Map<String, Filter> filtersMap = null;
    private List<String> subFilterNames;

    public FilterManager(PagedList pagedList) {
        this.list = pagedList;
        obligatoriesFilters = new LinkedList<String>();
        subFilterNames = new LinkedList<String>();
        Filter rootFilter = list.getFilter();
        fillFilterNames(rootFilter);
    }

    public List<String> getObligatoriesFilters() {
        return obligatoriesFilters;
    }

    public Map<String, Filter> getFiltersMap() {
        if (filtersMap == null) {
            filtersMap = new HashMap<String, Filter>();
            Filter filter = list.getFilter();
            filtersMap = getFiltersMap(filter, filtersMap, "");
        }
        return filtersMap;
    }

    private Map<String, Filter> getFiltersMap(Filter filter, Map<String, Filter> map, String parentName) {
        Map<String, Filter> result = map;
        String parent = parentName;

        result.put(parent, filter);
        if ((filter instanceof CompositeFilter) && !(filter instanceof OneValueCompositeFilter)) {
            CompositeFilter compFilter = (CompositeFilter) filter;
            Collection subFilterNames = compFilter.getSubFilterNames();
            for (Iterator iterator = subFilterNames.iterator(); iterator.hasNext(); ) {
                String filterName = (String) iterator.next();
                parent = /*(parent.length() > 0 ? parent + "." : "") +*/ filterName;
                result = getFiltersMap(compFilter.getSubFilter(filterName), result, parent);
            }
        }
        return result;
    }

    private void fillFilterNames(Filter filter) {
        if (filter instanceof CompositeFilter) {
            CompositeFilter compFilter = (CompositeFilter) filter;
            for (Iterator iterator = compFilter.getSubFilters().iterator(); iterator.hasNext(); ) {
                Filter subFilter = (Filter) iterator.next();
                fillFilterNames(subFilter);
            }
            Set filterNames = compFilter.getObligatoriesFilters();
            for (Iterator iterator = filterNames.iterator(); iterator.hasNext(); ) {
                String filterName = (String) iterator.next();
                if (!obligatoriesFilters.contains(filterName)) {
                    obligatoriesFilters.add(filterName);
                }
            }
            filterNames = new HashSet();
            filterNames.addAll(compFilter.getSubFilterNames());

            for (Iterator iterator = filterNames.iterator(); iterator.hasNext(); ) {
                String filterName = (String) iterator.next();
                if (!obligatoriesFilters.contains(filterName)) {
                    subFilterNames.add(filterName);
                }
            }
        }
    }

    public List<String> getSubFilterNames() {
        return subFilterNames;
    }

    public void addFilter(String filterName, Filter filter) {
        Filter rootFilter = list.getFilter();
        if (rootFilter instanceof CompositeFilter) {
            ((CompositeFilter) rootFilter).addFilter(filterName, filter);
        } else {
            list.setFilter(filter);
        }
    }
}