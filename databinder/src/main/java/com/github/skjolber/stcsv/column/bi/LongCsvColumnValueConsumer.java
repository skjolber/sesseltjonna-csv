package com.github.skjolber.stcsv.column.bi;

import java.util.function.ObjLongConsumer;

public class LongCsvColumnValueConsumer<T> implements CsvColumnValueConsumer<T> {

	protected final static long L_BILLION = 1000000000;

    protected final static String MIN_LONG_STR_NO_SIGN = String.valueOf(Long.MIN_VALUE).substring(1);
    protected final static String MAX_LONG_STR = String.valueOf(Long.MAX_VALUE);

	protected final ObjLongConsumer<T> setter;
		
	public LongCsvColumnValueConsumer(ObjLongConsumer<T> setter) {
		this.setter = setter;
	}

	@Override
	public void consume(T object, char[] array, int start, int end) {
		setter.accept(object, parseLong(array, start, end));
	}

    public static long parseLong(char[] ch, int off, int end) {
    	boolean neg = (ch[off] == '-');
    	if(neg) {
    		off++;
    	}
    	
    	int len = end - off;
    	
    	if(len > 9) {
             int len1 = len-9;
    		
    		 long val = parseInteger(ch, off, len1) * L_BILLION + parseInteger(ch, off+len1, 9);    		
    		 
    		 return neg ? -val : val;
    	} else if(len > 0) {
    		long val =  parseInteger(ch, off, end - off);
    		
    		return neg ? -val : val;
    	} else {
    		throw new NumberFormatException();
    	}
    }

    /**
     * Fast method for parsing integers that are known to fit into
     * regular 32-bit signed int type. This means that length is
     * between 1 and 9 digits (inclusive)
     * 
	 * @param ch array
	 * @param off start index (inclusive)
	 * @param len length 
     * @return parsed value
     */
    
    protected static int parseInteger(char[] ch, int off, int len)
    {
        int num = ch[off + len - 1] - '0'; // LSD
        
        switch(len) {
        case 9: 
            num += (ch[off++] - '0') * 100000000;
        case 8: 
            num += (ch[off++] - '0') * 10000000;
        case 7: 
            num += (ch[off++] - '0') * 1000000;
        case 6: 
            num += (ch[off++] - '0') * 100000;
        case 5: 
            num += (ch[off++] - '0') * 10000;
        case 4: 
            num += (ch[off++] - '0') * 1000;
        case 3: 
            num += (ch[off++] - '0') * 100;
        case 2: 
            num += (ch[off] - '0') * 10;
        default: return num;
        }
    }
    

    

}
