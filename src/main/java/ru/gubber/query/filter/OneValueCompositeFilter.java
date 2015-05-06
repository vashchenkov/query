package ru.gubber.query.filter;

import org.hibernate.Query;
import org.hibernate.type.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Gubber
 * Date: 12.10.13
 * Time: 20:05
 */
public class OneValueCompositeFilter extends CompositeFilter implements SingleFilter {

    private Type type;
    List values = new ArrayList();
    private boolean allowNulls = false;

    public OneValueCompositeFilter(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public void setAllowNulls(boolean allowNulls) {
        this.allowNulls = allowNulls;
    }

    @Override
    public void clear() {
        values.clear();
        super.clear();
    }

    @Override
    public int fillParameters(Query query, int start) {
        if (isEmpty()) {
            return 0;
        }
        return super.fillParameters(query, start);
    }

    @Override
    public void addValue(Object value) {

        clear();
        if ((!allowNulls) || (value != null)) {
            values.add(value);
            for (String filterName : filters.keySet()) {
                if (!obligatoriesFilters.contains(filterName)) {
                    Filter filter = filters.get(filterName);
                    filter.addValue(value);
                }
            }
        }
    }

    @Override
    public boolean isEmpty() {
        return values.size() == 0;
    }

    @Override
    public Object getValue() {
        return values.size() > 0 ? values.get(0) : null;
    }

    @Override
    public List getValues() {
        return values;
    }
}