package com.github.skjolber.stcsv.column;

import java.util.function.ObjLongConsumer;

public class LongCsvColumnValueConsumer<T> implements CsvColumnValueConsumer<T> {

    final static long L_BILLION = 1000000000;

    final static String MIN_LONG_STR_NO_SIGN = String.valueOf(Long.MIN_VALUE).substring(1);
    final static String MAX_LONG_STR = String.valueOf(Long.MAX_VALUE);

	protected final ObjLongConsumer<T> setter;
		
	public LongCsvColumnValueConsumer(ObjLongConsumer<T> setter) {
		this.setter = setter;
	}

	@Override
	public void consume(T object, char[] array, int start, int end) {
		setter.accept(object, parseLong(array, start, end));
	}

    public static long parseLong(char[] ch, int off, int end) {
    	int len = end - off;
    	
    	if(len > 9) {
             int len1 = len-9;
    		
    		 long val = parseInteger(ch, off, len1) * L_BILLION;    		

    		 return val + (long) parseInteger(ch, off+len1, 9);
    	} else {
    		return (long) parseInteger(ch, off, end);
    	}
    }

    /**
     * Fast method for parsing integers that are known to fit into
     * regular 32-bit signed int type. This means that length is
     * between 1 and 9 digits (inclusive)
     *<p>
     * Note: public to let unit tests call it
     * 
	 * @param ch array
	 * @param off start index (inclusive)
	 * @param end end index (exclusive)
     * @return parsed value
     */
    
    public static int parseInteger(char[] ch, int off, int end) {
    	// https://stackoverflow.com/questions/1030479/most-efficient-way-of-converting-string-to-integer-in-java
    	
    	int len = end - off;
        int num = ch[off] - '0';

        if (len > 4) {
            num = (num * 10) + (ch[++off] - '0');
            num = (num * 10) + (ch[++off] - '0');
            num = (num * 10) + (ch[++off] - '0');
            num = (num * 10) + (ch[++off] - '0');
            len -= 4;
            if (len > 4) {
                num = (num * 10) + (ch[++off] - '0');
                num = (num * 10) + (ch[++off] - '0');
                num = (num * 10) + (ch[++off] - '0');
                num = (num * 10) + (ch[++off] - '0');
                return num;
            }
        }
        if (len > 1) {
            num = (num * 10) + (ch[++off] - '0');
            if (len > 2) {
                num = (num * 10) + (ch[++off] - '0');
                if (len > 3) {
                    num = (num * 10) + (ch[++off] - '0');
                }
            }
        }
        return num;
    }

    

}
