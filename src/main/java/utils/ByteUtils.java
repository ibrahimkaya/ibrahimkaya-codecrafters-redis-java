package utils;

import com.google.common.primitives.Ints;

import static java.nio.ByteBuffer.allocate;

public class ByteUtils {
    private ByteUtils() {
    }

    public static Integer bytesToInt(byte[] bytes) {
        return Ints.fromByteArray(bytes);
    }

    public static <T> byte[] wrapWithBytes(T value) {
        return switch (value) {
            case Short s -> allocate(2).putShort(s).array();
            case Integer i -> allocate(4).putInt(i).array();
            default -> throw new IllegalStateException("Unexpected value: " + value);
        };
    }
}