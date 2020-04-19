package com.example.hashbasedoutputstream;

import net.jqwik.api.*;
import net.jqwik.api.constraints.IntRange;
import org.assertj.core.api.*;

class PropertyBasedTests {

	@Property
	boolean absoluteValueOfAllNumbersIsPositive(@ForAll @IntRange(min = Integer.MIN_VALUE + 1) int anInteger) {
		return Math.abs(anInteger) >= 0;
	}

	@Property
	void lengthOfConcatenatedStringIsGreaterThanLengthOfEach(
		@ForAll String string1, @ForAll String string2
	) {
		String conc = string1 + string2;
		Assertions.assertThat(conc.length()).isGreaterThanOrEqualTo(string1.length());
		Assertions.assertThat(conc.length()).isGreaterThanOrEqualTo(string2.length());
	}
}
