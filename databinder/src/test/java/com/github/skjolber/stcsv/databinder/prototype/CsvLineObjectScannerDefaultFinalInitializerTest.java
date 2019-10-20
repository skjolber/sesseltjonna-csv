package com.github.skjolber.stcsv.databinder.prototype;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;

public class CsvLineObjectScannerDefaultFinalInitializerTest {

	@Test
	public void testScanner() throws Exception {
		assertThat(CsvLineObjectScannerDefaultFinalInitializer.getStringValue0()).isNotNull();
	}

}
