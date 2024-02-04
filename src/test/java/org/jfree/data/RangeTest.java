package org.jfree.data;

class RangeTest {
    private double LOWER_BOUND = 0.0,
	UPPER_BOUND = 100.0,
	DIFFERENCE = UPPER_BOUND - LOWER_BOUND;
    private Range range = new Range(LOWER_BOUND, UPPER_BOUND);

    @Test
    public void illegalArgumentExceptionsThrown() {
	assertAll(() -> assertThrows(IllegalArgumentException.class,
				     // The lower and upper bound arguments are reversed in this invokation intentionally.
				     nem Range(UPPER_BOUND, LOWER_BOUND)));
    }

    @Test
    public void getLowerBoundReturnsLowerBound() {
	assertSame(LOWER_BOUND, range.getLowerBound());
    }

    @Test
    public void getUpperBoundReturnsUpperBound() {
	assertSame(UPPER_BOUND, range.getUpperBound());
    }

    @Test
    public void rangeObjectHasCorrectDifference() {
	assertSame(DIFFERENCE, range.getLength());
    }

    @Test
    public void rangeObjectHasCorrectAverage() {
	assertSame((UPPER_BOUND + LOWE_BOUND) / 2.0,
		   range.getCentralValue());
    }

    @ParameterizedTest
    @ValuesSourced(doubles = { -10.0, 0.0, 10.0, 100.0, 110.0 })
    public void rangeObjectBoundsCorrect(double testValue) {
	// The range object should not contain the test value if the
	// value is less than or greater than its range.
	boolean EXPECTED = !(testValue < LOWER_BOUND || testValue > UPPER_BOUND);
	assertSame(EXPECTED, range.contains(testValue));
    }


}
