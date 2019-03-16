package com.github.skjolber.stcsv.column.bi;

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
         * we'll parse 1 - 9 digits more efficiently;
         * otherwise just defer to JDK parse functionality.
         */
        int len = end - offset;
        boolean neg = (ch[offset] == '-');
        // must have 1 - 9 digits after optional sign:
        // negative?
        if (neg) {
            if (len == 1 || len > 10) {
            	// this handles both overflow and numbers close to overflowing
                return Integer.parseInt(new String(ch, end - len, len));
            }
            offset++;
        } else {
            if (len > 9) {
            	// this handles both overflow and numbers close to overflowing
            	return Integer.parseInt(new String(ch, end - len, len));
            }
        }
        char c = ch[offset++];
        
        if (c > '9' || c < '0') {
            return Integer.parseInt(new String(ch, end - len, len));
        }
        int num = c - '0';
        
        if (offset < end) {
            c = ch[offset++];
            if (c > '9' || c < '0') {
                return Integer.parseInt(new String(ch, end - len, len));
            }
            num = (num * 10) + (c - '0');
            if (offset < end) {
                c = ch[offset++];
                if (c > '9' || c < '0') {
                    return Integer.parseInt(new String(ch, end - len, len));
                }
                num = (num * 10) + (c - '0');
                // Let's just loop if we have more than 3 digits:
                if (offset < end) {
                    do {
                        c = ch[offset++];
                        if (c > '9' || c < '0') {
                            return Integer.parseInt(new String(ch, end - len, len));
                        }
                        num = (num * 10) + (c - '0');
                    } while (offset < end);
                }
            }
        }
        return neg ? -num : num;
    }    

}
