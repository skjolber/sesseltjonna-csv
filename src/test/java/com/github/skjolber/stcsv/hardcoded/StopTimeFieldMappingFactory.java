/**
 * Copyright (C) 2011 Brian Ferris <bdferris@onebusaway.org>
 * Copyright (C) 2011 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.skjolber.stcsv.hardcoded;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StopTimeFieldMappingFactory {

  private static DecimalFormat _format = new DecimalFormat("00");

  private static Pattern _pattern = Pattern.compile("^(-{0,1}\\d+):(\\d{2}):(\\d{2})$");

  public static String getSecondsAsString(int t) {
    int seconds = positiveMod(t, 60);
    int hourAndMinutes = (t - seconds) / 60;
    int minutes = positiveMod(hourAndMinutes, 60);
    int hours = (hourAndMinutes - minutes) / 60;

    StringBuilder b = new StringBuilder();
    b.append(_format.format(hours));
    b.append(":");
    b.append(_format.format(minutes));
    b.append(":");
    b.append(_format.format(seconds));
    return b.toString();
  }

  private static final int positiveMod(int value, int modulo) {
    int m = value % modulo;
    if (m < 0) {
      m += modulo;
    }
    return m;
  }

  public static int getStringAsSeconds(String value) {
    Matcher m = _pattern.matcher(value);
    if (!m.matches())
      throw new IllegalArgumentException(value);
    try {
      int hours = Integer.parseInt(m.group(1));
      int minutes = Integer.parseInt(m.group(2));
      int seconds = Integer.parseInt(m.group(3));

      return seconds + 60 * (minutes + 60 * hours);
    } catch (NumberFormatException ex) {
      throw new IllegalArgumentException(value);
    }
  }
}