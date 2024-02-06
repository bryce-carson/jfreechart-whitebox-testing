package org.jfree.data;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.jfree.data.Range.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class RangeTest {
    private double LOWER_BOUND = 0.0,
	UPPER_BOUND = 100.0,
	DIFFERENCE = UPPER_BOUND - LOWER_BOUND;
    private Range range = new Range(LOWER_BOUND, UPPER_BOUND);

    @Test
    public void illegalArgumentExceptionsThrown() {
	assertAll(() -> assertThrows(IllegalArgumentException.class,
				     // The lower and upper bound arguments are reversed in this invocation intentionally.
				     () -> new Range(UPPER_BOUND, LOWER_BOUND)));
    }

    @Test
    public void getLowerBoundReturnsLowerBound() {
	assertEquals(LOWER_BOUND, range.getLowerBound());
    }

    @Test
    public void getUpperBoundReturnsUpperBound() {
	assertEquals(UPPER_BOUND, range.getUpperBound());
    }

    @Test
    public void rangeObjectHasCorrectDifference() {
	assertEquals(DIFFERENCE, range.getLength());
    }

    @Test
    public void rangeObjectHasCorrectAverage() {
	assertEquals((UPPER_BOUND + LOWER_BOUND) / 2.0,
		   range.getCentralValue());
    }

    @ParameterizedTest
    @ValueSource(doubles = { -10.0, 0.0, 10.0, 100.0, 110.0 })
    public void rangeObjectBoundsCorrect(double testValue) {
	// The range object should not contain the test value if the
	// value is less than or greater than its range.
	boolean EXPECTED = !(testValue < LOWER_BOUND || testValue > UPPER_BOUND);
	assertSame(EXPECTED, range.contains(testValue));
    }

    /**
     * In order of their definition in the method below, the assertions are:
     * <ol>
     * <li>An exact overlap of the ranges,</li>
     * <li>An overlap of the lower boundary,</li>
     * <li>An overlap of the upper boundary,</li>
     * <li>An overlap within boundaries of the dispatching instance,</li>
     * <li>An overlap, beyond both boundaries,</li>
     * <li>Non-overlap beyond the upper boundary,</li>
     * <li>Non-overlap before the lower boundary.</li>
     * <li>Non-overlap, with illogical parameters,</li>
     * <li>Non-overlap, again with illogical parameters.</li>
     * </ol>
     */
	@Test
	public void intersectionBoundaryTruthTable() {
		assertAll(() -> { assertTrue(range.intersects(LOWER_BOUND, UPPER_BOUND)); },
				() -> { assertTrue(range.intersects(LOWER_BOUND - 10.0, LOWER_BOUND + 10.0)); },
				() -> { assertTrue(range.intersects(UPPER_BOUND - 10.0, UPPER_BOUND + 10.0)); }, // Fails because first condition of second branch in implementation is "upper" when it should be "lower".
				() -> { assertTrue(range.intersects(LOWER_BOUND + 10.0, UPPER_BOUND - 10.0)); },
				() -> { assertTrue(range.intersects(LOWER_BOUND - 10.0, UPPER_BOUND + 10.0)); },
			() -> { assertFalse(range.intersects(UPPER_BOUND + 10.0, UPPER_BOUND + 20.0)); },
			() -> { assertFalse(range.intersects(LOWER_BOUND - 20.0, LOWER_BOUND - 10.0)); },
			() -> { assertFalse(range.intersects(UPPER_BOUND + 20.0, UPPER_BOUND + 10.0)); },
			() -> { assertFalse(range.intersects(UPPER_BOUND, LOWER_BOUND)); }
		);
	}

	// NOTE: the missed branch is unreachable because the inner condition is unnecessary, and the code is unreachable. 
	@Test
	public void rangeConstraintTruthTable() {
		assertAll(() -> { assertEquals(UPPER_BOUND, range.constrain(UPPER_BOUND + 1.0)); },
				() -> { assertEquals(LOWER_BOUND, range.constrain(LOWER_BOUND - 1.0)); },
				() -> { assertEquals(UPPER_BOUND - 1.0, range.constrain(UPPER_BOUND - 1.0)); },
				() -> { assertEquals(LOWER_BOUND + 1.0, range.constrain(LOWER_BOUND + 1.0)); }
				);
	}
	
	@Test
	public void saneReturnValuesFromCombine() {
		assertAll(() -> { assertNull(Range.combine(null, null)); },
				
				() -> { assertNotNull(Range.combine(range, range)); },
				() -> { assertNotNull(Range.combine(range, null)); },
				() -> { assertNotNull(Range.combine(null, range)); },

				() -> { assertEquals(range, Range.combine(range, range)); },
				() -> { assertEquals(range, Range.combine(null, range)); },
				() -> { assertEquals(range, Range.combine(range, null)); },
				
				() -> { assertEquals(new Range(-UPPER_BOUND, UPPER_BOUND), Range.combine(range, new Range(-UPPER_BOUND, LOWER_BOUND))); }
				);
	}

	/** Technically, the specification does not imply that the
	 * same range object will be returned if the potential value
	 * to be included in the range is already contained by the
	 * range object. Therefore, <code>assertEquals</code> is used
	 * and not <code>assertSame</code>.
	 */
	@Test
	public void saneRangeObjectsReturnedFromExpandToInclude() {
		assertAll(() -> { assertEquals(range, expandToInclude(range, 25.0)); },
			  () -> { assertNotEquals(new Range(25.0, 25.0), expandToInclude(null, 25.0)); },
			  () -> { assertTrue(expandToInclude(range, 125.0).contains(125.0)); },
			  () -> { assertTrue(expandToInclude(range, -25.0).contains(-25.0)); });
	}

	/** Does the method handle negative percentages gracefully? */
	@ParameterizedTest
	@ValueSource(doubles = { -1.50, 0.00, 0.10, 0.25, 0.50, 0.75, 1.00, 1.25 })
	public void saneRangeObjectsReturnsFromExpand(double percentage) {
		double lower = range.length() * percentage,
			upper = lower;
		/** With <code>range</code> from the Class scope
		 * having a length of one hundred, the first parameter
		 * for this test should give a lower margin of
		 * negative one hundred and fifty; the lower bound of
		 * the input range, being zero, will be increased to
		 * positive one hundred and fifty. Similarly, the
		 * upper bound of the input range will be decreased to
		 * negative fifty. This should produce an error, but
		 * the constructor for Range is not robust. */
		Range expandedRange = new Range(range.getLowerBound() - lower,
						range.getUpperBound() + upper);

		assertAll(() -> {
				/** This assertion should only be
				 * tested when the parameter is a
				 * negative percentage (-1.50), so an
				 * assumption is placed here to
				 * prevent it from being tested with
				 * every parameter value. */
				assumeTrue(lower > upper);
				assertThrows(IllegalArgumentException.class,
					     () -> { expand(range, lower, upper); });
			},
			() -> { assertThrows(IllegalArgumentException.class,
					     () -> {
						     expand(null, percentage, percentage);
					     });
			});
	}
}
