package org.jfree.data;

import static org.jfree.data.DataUtilities.calculateColumnTotal;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.io.CSV;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.ValueSource;

class DataUtiltiesTest {
    final public int HOUSE_PRICES_ROW_COUNT = 546;
    public static DefaultCategoryDataset housePrices, housePricesModified, housePricesEmptyTable;

    /**
     * @throws IOException
     * @throws FileNotFoundException
     */
    @BeforeAll
    public static void setup() throws IOException, FileNotFoundException {
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
     */
    @ParameterizedTest
    @ValueSource(strings = { "housePricesModified", "housePricesInvalidated"})
    public void zeroReturned(String fieldName) throws NoSuchFieldException, IllegalAccessException {
        final Field field = getClass().getField(fieldName);
        final DefaultCategoryDataset dataset = (DefaultCategoryDataset) field.get(field);
        final int COLUMN = 0;
        final double COLUMN_SUM = 149331.0;

        assertSame(calculateColumnTotal(dataset, COLUMN), COLUMN_SUM);
    }

    /**
     * On the assumption that a NULL value in a CSV file is invalid, because the
     * specification is ambiguous, the method should throw an {@link java.security.InvalidParameterException}.
     *
     * @param fieldName the suffix to append to the name of the field in
     * <code>DataUtilitiesTest</code> to use for the repeated tests.
     */
    @ParameterizedTest
    @ValueSource(strings = { "housePricesModified", "housePricesInvalidated"})
    public void invalidParameterExceptionThrown(String fieldName) throws NoSuchFieldException, IllegalAccessException {
        final Field field = getClass().getField(fieldName);
        final DefaultCategoryDataset dataset = (DefaultCategoryDataset) field.get(field);
        final int COLUMN = 0;

        assertThrows(java.security.InvalidParameterException.class,
                () -> calculateColumnTotal(dataset, COLUMN));
    }

    /**
     * The method should be able to calculate the correct sum for each column.
     * Given that the test input file has different sums for each column, any
     * incorrect result means that the method is either calculating the correct
     * sum of the incorrect column, or it is not calculating the correct sum for
     * the correct column. Either possibility is a defect.
     *
     * @param expected The test oracle
     * @param column The column for which the oracle value applies.
     */
    @ParameterizedTest
    @CsvFileSource(resources = "HousePricesColumnSums.csv")
    public void correctColumnSums(Double expected, int column) {
        assertSame(calculateColumnTotal(housePrices, column), expected);
    }
}
