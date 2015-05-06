package ru.gubber.query.filter;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import ru.gubber.query.PagedList;

/**
 */
public abstract class AbstractFilter implements Filter {
    private static Logger logger = LogManager.getLogger(AbstractFilter.class);

    private boolean mandatory;

    protected boolean changed;

    private String alias ;//= PagedList.ALIAS;

    protected String[] filterNames;

    protected AbstractFilter() {
        this.alias = PagedList.ALIAS;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public Filter removeSubFilter(String name) {
        return null;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
