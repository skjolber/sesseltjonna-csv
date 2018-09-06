package com.github.skjolber.stcsv.prototype;

import org.junit.jupiter.api.Test;
import static com.google.common.truth.Truth.assertThat;

public class CsvLineObjectScannerDefaultFinalInitializerTest {

	@Test
	public void testScanner() throws Exception {
		assertThat(CsvLineObjectScannerDefaultFinalInitializer.getStringValue0()).isNotNull();
	}

}
