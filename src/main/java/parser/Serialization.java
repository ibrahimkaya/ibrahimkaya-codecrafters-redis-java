package parser;

/**
 * <a href="https://redis.io/docs/latest/develop/reference/protocol-spec/">Redis serialization protocol specification</a>
 * example for ping is *1\r\n$4\r\nPING\r\n which is
 * array 1
 * CRLF
 * BULK STRING 4 CHAR
 * CRLF
 */
public class Serialization {
    public static final byte[] CRLF = "\r\n".getBytes();
    public static final byte[] SIMPLE_STRING_PREFIX = "+".getBytes();
    public static final byte[] SIMPLE_ERROR_PREFIX = "-".getBytes();
    public static final byte[] ARRAY = "*".getBytes();
    public static final byte[] BULK_STRING = "$".getBytes();

    public static byte[] decorateString(String string) {
        return mergeByteArrays(SIMPLE_STRING_PREFIX, string.getBytes(), CRLF);
    }

    public static byte[] decorateError(String error) {
        return mergeByteArrays(SIMPLE_ERROR_PREFIX, error.getBytes(), CRLF);
    }

    /**
     * $5\r\nhello\r\n
     * $<length>\r\n<data>\r\n
     */
    public static byte[] decorateBulkString(String bulkString) {
        return mergeByteArrays(BULK_STRING, Integer.toString(bulkString.length()).getBytes(), CRLF, bulkString.getBytes(), CRLF);
    }

    /**
     * *<number-of-elements>\r\n<element-1>...<element-n>
     * *2\r\n$5\r\nhello\r\n$5\r\nworld\r\n
     */
    public static byte[] decorateArray(String... elements) {
        byte[] arrayPrefixBytes = mergeByteArrays(ARRAY, Integer.toString(elements.length).getBytes(), CRLF);
        for (String string : elements) {
            byte[] decoratedElements = decorateBulkString(string);
            arrayPrefixBytes = mergeByteArrays(arrayPrefixBytes, decoratedElements);
        }
        return arrayPrefixBytes;
    }

    private static byte[] mergeByteArrays(byte[]... arrays) {
        int totalLength = 0;
        for (byte[] array : arrays) {
            totalLength += array.length;
        }

        byte[] result = new byte[totalLength];
        int pos = 0;
        for (byte[] array : arrays) {
            for (byte b : array) result[pos++] = b;
        }

        return result;
    }
}
