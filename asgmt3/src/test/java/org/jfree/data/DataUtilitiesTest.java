package org.jfree.data;

import static org.jfree.data.DataUtilities.calculateColumnTotal;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.io.CSV;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.ValueSource;

class DataUtiltiesTest {
    final public int HOUSE_PRICES_ROW_COUNT = 542;
    public DefaultCategoryDataset housePrices, housePricesModified, housePricesEmptyTable;

    @BeforeAll
    public void setup() throws IOException, FileNotFoundException {
        housePrices = (DefaultCategoryDataset) new CSV()
            .readCategoryDataset(new FileReader("HousePrices.csv"));

        housePricesModified = (DefaultCategoryDataset) new CSV()
            .readCategoryDataset(new FileReader("HousePricesModified.csv"));
    }

    /**
     * On the assumption that a NULL value in a CSV file is invalid, because the
     * specification is ambiguous, the method should return zero.
     *
     * @param fieldNameSuffix the suffix to append to the name of the field in
     * <code>DataUtilitiesTest</code> to use for the repeated tests.
     *
     * FIXME: the use of reflection is unnecessary for this particular test; however, it does shorten the test and make it more readable.
     */
    @ParameterizedTest
    @ValueSource(strings = { "", "modified", "Invalidated"})
    public void invalidDataCausesException(String fieldNameSuffix) throws NoSuchFieldException, IllegalAccessException {
        String fieldName = "housePrices" + fieldNameSuffix;
        Field field = getClass().getField(fieldName);
        DefaultCategoryDataset dataset = (DefaultCategoryDataset) field.get(field);

        fail("Not yet reimplemented!");
    }

    @Test
    public void correctSum() {
        // The zero-indexed column to sum the values of: for this column, it
        // happens to be the values of the row numbers, so the formula for the
        // integer series sum 1 + 2 + 3 + ⋯ + 𝑛 is applicable here to generate
        // the test oracle using AWK or any other language (even actual
        // arithmetic could be used).
        final int COLUMN = 0;
        final double COLUMN_ZERO_SUM = 123456789.0;
        assertSame(calculateColumnTotal(housePrices, COLUMN), COLUMN_ZERO_SUM);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "ColumnSums.csv")
    public void somethingOrOther() {
        fail("Not yet reimplemented!");
    }
}
