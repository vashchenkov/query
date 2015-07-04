package ru.gubber.query;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import ru.gubber.query.model.TestBean;
import ru.gubber.query.sorter.FieldSorter;
import ru.gubber.query.sorter.Sorter;

/**
 * Created by gubber on 04.07.2015.
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class PagedListTest extends TestCase {

    @Test
    public void testMultiSorters(){
        PagedList pagedList = new PagedList(TestBean.class);

        pagedList.addSorter(new FieldSorter("name", Sorter.ORDER_ASCENDING));
        pagedList.addSorter(new FieldSorter("caption", Sorter.ORDER_DESCENDING));

        StringBuilder sb = new StringBuilder();
        pagedList.generateSorting(sb);

        assertEquals(" order by foo.name asc, foo.caption desc", sb.toString().toLowerCase());
    }

}