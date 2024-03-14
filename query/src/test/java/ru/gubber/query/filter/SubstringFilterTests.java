package ru.gubber.query.filter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты для SubstringFilter")
class SubstringFilterTests {

    @DisplayName("Необходимо корректно формировать строчку поиска для второго фильтра по подстроке игнорируя регистр")
    @Test
    public void correctHQLForSecondCaseInsensitive() {
        String filterQuery = "(cast (foo.name as string) LIKE :attribute_1 )";
        String filterPrefix = filterQuery + " OR ";
        StringBuilder sb = new StringBuilder(filterPrefix);
        SubstringFilter sut = new SubstringFilter("name", "qqq", false);
        sut.appendFilterCondition(sb, 2);

        assertEquals(filterPrefix + "lower(cast (foo.name as string)) LIKE lower(:attribute_2) ", sb.toString());
    }
}