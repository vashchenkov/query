package ru.gubber.query.filter;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import ru.gubber.query.PagedList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gubber on 29.09.2015.
 */
public class EmptyFieldFilter implements Filter{
	private static Logger logger = LogManager.getLogger(IntervalFilter.class);
	/**
	 * Алиас объекта на свойство которого накладывается фильтр
	 */
	private String alias = PagedList.ALIAS;

	/**
	 * имя фильтруемого поля
	 */
	private String fieldName;

	@Override
	public void setAlias(String alias) {
		this.alias = alias;
	}

	@Override
	public String getAlias() {
		return alias;
	}

	@Override
	public Filter removeSubFilter(String name) {
		return null;
	}

	@Override
	public Filter getSubFilter(String name) {
		return null;
	}

	@Override
	public List getSubFilters() {
		return new ArrayList();
	}

	@Override
	public List getValues() {
		return new ArrayList();
	}

	@Override
	public void clear() {

	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public int appendFilterCondition(StringBuilder sb, int filterCount) {
		if (isEmpty())
			return 0;
			sb = sb.append(alias).append(".").append(fieldName);
		sb = sb.append(" is null");
		if (logger.isDebugEnabled())
			logger.debug("where condition = " + sb.toString());

		return 0;
	}

	@Override
	public int fillParameters(Query query, int start) {
		return 0;
	}

	@Override
	public boolean accept(Object object) {
		return false;
	}

	@Override
	public boolean isMandatory() {
		return false;
	}

	@Override
	public void addValue(Object value) {

	}
}