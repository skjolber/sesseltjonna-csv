package com.github.skjolber.stcsv.column;

import java.util.function.ObjIntConsumer;

public class IntCsvColumnValueConsumer<T> implements CsvColumnValueConsumer<T> {

	protected final ObjIntConsumer<T> consumer;
		
	public IntCsvColumnValueConsumer(ObjIntConsumer<T> consumer) {
		this.consumer = consumer;
	}

	@Override
	public void consume(T object, char[] array, int start, int end) {
		consumer.accept(object, parseInt(array, start, end));
	}

    /**
     * Helper method to (more) efficiently parse integer numbers from
     * String values.
     * 
	 * @param ch array
	 * @param offset start index (inclusive)
	 * @param end end index (exclusive)
	 * @return the parsed value
	 */
	
    public static int parseInt(char[] ch, int offset, int end) {
        /* Ok: let's keep strategy simple: ignoring optional minus sign,
         * we'll accept 1 - 9 digits and parse things efficiently;
         * otherwise just defer to JDK parse functionality.
         */
        int len = end - offset;
        boolean neg = (ch[offset] == '-');
        
        // must have 1 - 9 digits after optional sign:
        // negative?
        if (neg) {
            if (len == 1 || len > 10) {
                return Integer.parseInt(new String(ch, offset, end - offset));
            }
            offset++;
        } else {
            if (len > 9) {
            	Integer.parseInt(new String(ch, offset, end - offset));
            }
        }
        char c = ch[offset++];
        
        if (c > '9' || c < '0') {
            return Integer.parseInt(new String(ch, offset, end - offset));
        }
        int num = c - '0';
        if (offset < len) {
            c = ch[offset++];
            if (c > '9' || c < '0') {
                return Integer.parseInt(new String(ch, offset, end - offset));
            }
            num = (num * 10) + (c - '0');
            if (offset < len) {
                c = ch[offset++];
                if (c > '9' || c < '0') {
                    return Integer.parseInt(new String(ch, offset, end - offset));
                }
                num = (num * 10) + (c - '0');
                // Let's just loop if we have more than 3 digits:
                if (offset < len) {
                    do {
                        c = ch[offset++];
                        if (c > '9' || c < '0') {
                            return Integer.parseInt(new String(ch, offset, end - offset));
                        }
                        num = (num * 10) + (c - '0');
                    } while (offset < len);
                }
            }
        }
        return neg ? -num : num;
    }    

}
