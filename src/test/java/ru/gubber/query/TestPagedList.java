package ru.gubber.query;

import ru.gubber.query.filter.Filter;

/**
 * Created by gubber on 04.07.2015.
 */
public class TestPagedList extends PagedList{
    public TestPagedList(Class classToQuery) {
        super(classToQuery);
    }

    public TestPagedList(Class classToQuery, Filter filter) {
        super(classToQuery, filter);
    }


}
