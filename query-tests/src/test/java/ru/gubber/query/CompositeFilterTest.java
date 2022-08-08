package ru.gubber.query;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import ru.gubber.query.filter.*;

/**
 * Тестирование корректной работы композитного фильтра
 * Created by gubber on 22.02.2017.
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class CompositeFilterTest extends TestCase {
	private final static EmptyFieldFilter testFieldFilter = new EmptyFieldFilter("testField");
	private static final String SIMPLE_FILTER = "simpleFilter";
	private static final IntervalFilter intervalFilter = new IntervalFilter("simpleComparableValue", null, null);
	private static final String SECOND_ROOT_FILTER = "secondRoot";
	private static final String INTERVAL_FILTER = "intervalFilter";

	@Test
	public void testFindFirstLevelSubFilter() {
		CompositeFilter filter = createSimpleCompositeFilter();

		Filter testFilter = filter.getSubFilter(SIMPLE_FILTER);

		assertEquals(testFieldFilter, testFilter);
	}

	@Test
	public void testFindUnknownFirstLevelSubFilter() {
		CompositeFilter filter = createSimpleCompositeFilter();

		Filter testFilter = filter.getSubFilter("unknownFilter");

		assertTrue("Should be found NullFilter", testFilter instanceof NullFilter);
	}

	@Test
	public void testFindSecondLevelSubFilter() {
		CompositeFilter root = createTwoLevelCompositeFilter();

		Filter testFilter = root.getSubFilter(SECOND_ROOT_FILTER + "/" + INTERVAL_FILTER);

		assertEquals(intervalFilter, testFilter);
	}

	@Test
	public void testFindWithNonCompositeSubFilter() {
		CompositeFilter root = createTwoLevelCompositeFilter();

		Filter testFilter = root.getSubFilter(SIMPLE_FILTER + "/" + INTERVAL_FILTER);

		assertTrue("Should be found NullFilter", testFilter instanceof NullFilter);
	}

	@Test
	public void testFindUnknownSecondLevelSubFilter() {
		CompositeFilter root = createTwoLevelCompositeFilter();

		Filter testFilter = root.getSubFilter(SECOND_ROOT_FILTER + "/" + SIMPLE_FILTER);

		assertTrue("Should be found NullFilter", testFilter instanceof NullFilter);
	}

	@Test
	public void testFindUnknownCompositeSubFilter() {
		CompositeFilter root = createTwoLevelCompositeFilter();

		Filter testFilter = root.getSubFilter("unknownCompositeFilter/" + INTERVAL_FILTER);

		assertTrue("Should be found NullFilter", testFilter instanceof NullFilter);
	}

	@Test
	public void testNullFilterName() {
		CompositeFilter root = createTwoLevelCompositeFilter();

		Filter testFilter = root.getSubFilter(null);

		assertTrue("Should be found NullFilter", testFilter instanceof NullFilter);
	}

	@Test
	public void shouldCalculate4AttributesWhenThreeFiltersPassIntoCompositeFilterWithCompositeFilterIsNotFirst() {
		CompositeFilter rootFilter = new CompositeFilter();

		IntervalFilter intF = new IntervalFilter("bithDate", 0, null);
		rootFilter.addFilter("birthFilter", intF);

		OneValueCompositeFilter nameFilter = new OneValueCompositeFilter();
		AsterickFilter fnFilter = new AsterickFilter("fullname", "xx");
		AsterickFilter nnFilter = new AsterickFilter("nickname", "xx");
		nameFilter.addFilter("fullnameFilter", fnFilter);
		nameFilter.addFilter("nicknameFilter", nnFilter);

		nameFilter.addValue("xxx");
		rootFilter.addFilter("identityFilter", nameFilter);

		ValueFilter aliveFilter = new ValueFilter("alive");
		aliveFilter.addValue(true);
		rootFilter.addFilter("aliveFilter", aliveFilter);

		StringBuilder sb = new StringBuilder();
		int filterCondition = rootFilter.appendFilterCondition(sb, 0);
		assertEquals(4, filterCondition );
	}

	@Test
	public void shouldCalculate4AttributesWhenThreeFiltersPassIntoCompositeFilterWithCompositeFilterIsFirst() {
		CompositeFilter rootFilter = new CompositeFilter();

		OneValueCompositeFilter nameFilter = new OneValueCompositeFilter();
		AsterickFilter fnFilter = new AsterickFilter("fullname", "xx");
		AsterickFilter nnFilter = new AsterickFilter("nickname", "xx");
		nameFilter.addFilter("fullnameFilter", fnFilter);
		nameFilter.addFilter("nicknameFilter", nnFilter);

		nameFilter.addValue("xxx");
		rootFilter.addFilter("identityFilter", nameFilter);

		IntervalFilter intF = new IntervalFilter("bithDate", 0, null);
		rootFilter.addFilter("birthFilter", intF);

		ValueFilter aliveFilter = new ValueFilter("alive");
		aliveFilter.addValue(true);
		rootFilter.addFilter("aliveFilter", aliveFilter);

		StringBuilder sb = new StringBuilder();
		int filterCondition = rootFilter.appendFilterCondition(sb, 0);
		assertEquals(4, filterCondition );
	}

	private CompositeFilter createSimpleCompositeFilter() {
		CompositeFilter filter = new CompositeFilter();
		filter.addFilter(SIMPLE_FILTER, testFieldFilter);
		return filter;
	}

	private CompositeFilter createTwoLevelCompositeFilter() {
		CompositeFilter root = createSimpleCompositeFilter();
		CompositeFilter secondRoot = new CompositeFilter();
		root.addFilter(SECOND_ROOT_FILTER, secondRoot);
		secondRoot.addFilter(INTERVAL_FILTER, intervalFilter);
		return root;
	}

}