package ru.gubber.query.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gubber.query.PagedList;

import jakarta.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gubber on 29.09.2015.
 */
public class EmptyFieldFilter implements Filter{
	private static Logger logger = LoggerFactory.getLogger(IntervalFilter.class);
	/**
	 * Алиас объекта на свойство которого накладывается фильтр
	 */
	private String alias = PagedList.ALIAS;

	/**
	 * имя фильтруемого поля
	 */
	private String fieldName;

	public EmptyFieldFilter(String fieldName) {
		this.fieldName = fieldName;
	}

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
	public int appendFilterCondition(StringBuilder sb, int attributesCount) {
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