package ru.gubber.query;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import ru.gubber.query.filter.IntervalFilter;

/**
 * Тестирование интервального фильтра
 * Created by gubber on 22.02.2017.
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class IntervalFilterTest extends TestCase{

	@Test
	public void twoIntValueAddTest() {
		IntervalFilter intervalFilter = new IntervalFilter("someField", null, null);
		intervalFilter.addValue(5);
		intervalFilter.addValue(10);

		assertEquals(5, intervalFilter.getSmallerValue());
		assertEquals(10, intervalFilter.getBiggerValue());
	}

	@Test
	public void oneIntValueAddTest() {
		IntervalFilter intervalFilter = new IntervalFilter("someField", null, null);
		intervalFilter.addValue(5);

		assertEquals(5, intervalFilter.getSmallerValue());
		assertNull(intervalFilter.getBiggerValue());
	}

	@Test
	public void nullAndIntValueAddTest() {
		IntervalFilter intervalFilter = new IntervalFilter("someField", null, null);
		intervalFilter.addValue(null);
		intervalFilter.addValue(5);

		assertEquals(2, intervalFilter.getValues().size());
		assertNull(intervalFilter.getSmallerValue());
		assertEquals(5, intervalFilter.getBiggerValue());
	}

	@Test
	public void threeIntValueAddTest() {
		IntervalFilter intervalFilter = new IntervalFilter("someField", null, null);
		intervalFilter.addValue(5);
		intervalFilter.addValue(10);
		intervalFilter.addValue(15);

		assertEquals(2, intervalFilter.getValues().size());
	}

}
