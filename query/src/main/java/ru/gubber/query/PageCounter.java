package ru.gubber.query;

import java.util.ArrayList;
import java.util.List;

/**
 * Счётчик страниц
 */
public class PageCounter {
    private int itemsPerPage;
    private int itemCount;
    public static final int PAGE_COUNT_TO_SELECT = 10;

    public PageCounter(int itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
    }

    public int getPageCount() {
        return (itemCount % itemsPerPage == 0) ? itemCount / itemsPerPage: itemCount / itemsPerPage + 1;
    }

    public List getPageNumbers(int currentPage) {
        int firstPage = (currentPage / PageCounter.PAGE_COUNT_TO_SELECT) * PageCounter.PAGE_COUNT_TO_SELECT;
        if (firstPage < 0) {
            firstPage = 0;
        }
        int lastPage = (currentPage / PageCounter.PAGE_COUNT_TO_SELECT + 1) * PageCounter.PAGE_COUNT_TO_SELECT;
        if (lastPage > getPageCount()) {
            lastPage = getPageCount();
        }
        List pageNumbers = new ArrayList();
        for (int i = firstPage; i < lastPage; i++) {
            pageNumbers.add(new Integer(i));
        }

        return pageNumbers;
    }

    public int getItemsPerPage() {
        return itemsPerPage;
    }

    public void setItemsPerPage(int itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }
}
