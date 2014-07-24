package org.hejki.sys.core.utils;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;

/**
 * Methods for byte conversions.
 *
 * @author Petr Hejkal
 */
public class ByteUtils {

    /**
     * Convert byte to unsigned 1 byte value.
     * @return 0 - 255
     */
    public static int toInt(byte b) {
        return b & 0xFF;
    }

    /**
     * Converts byte array of length 4 to signed 4 byte value.
     * @return -2^31 - 2^31-1
     */
    public static int toInt(byte... data) {
        return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).put(data).getInt(0);
    }

    /**
     * Converts byte array of length 4 to unsigned 4 byte value.
     * @return 0 - 255
     */
    public static long toUnsignedInt(byte... data) {
        return toInt(data) & 0xFFFFFFFF;
    }

    /**
     * Converts byte array of length 2 to signed 2 byte value.
     * @return -32_768 - 32_767
     */
    public static short toShort(byte... data) {
        return ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).put(data).getShort(0);
    }

    /**
     * Converts byte array of length 2 to unsigned 2 byte value.
     * @return 0 - 65_535
     */
    public static int toUnsignedShort(byte... data) {
        return toShort(data) & 0xFFFF;
    }

    /**
     * Converts unsigned 4 byte value to byte array.
     */
    public static byte[] fromUnsignedInt(int value) {
        ByteBuffer buffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);

        buffer.putInt(value & 0xFFFFFFFF);
        buffer.position(0);
        return buffer.array();
    }

    /**
     * Converts unsigned 2 byte value to byte array.
     */
    public static byte[] fromUnsignedShort(int value) {
        ByteBuffer buffer = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN);

        buffer.putShort((short) (value & 0xFFFF));
        buffer.position(0);
        return buffer.array();
    }

    /**
     * Check if specific bit in byte is set.
     */
    public static boolean isBitSet(byte b, int bitPosition) {
        return (b & (1L << bitPosition)) != 0;
    }

    /**
     * Check if specific bit in byte is set.
     */
    public static byte bitSet(int b, int bitPosition, boolean value) {
        if (value) {
            return (byte) (b | (1 << bitPosition));
        }
        return (byte) (b & ~(1 << bitPosition));
    }

    /**
     * Converts byte array to string.
     */
    public static String byteToString(byte... data) {
        CharBuffer buffer = ByteBuffer.wrap(data).asCharBuffer();
        return buffer.toString();
    }

    /**
     * Converts raw byte data to ascii string.
     */
    public static String toAscii(byte[] rawData) {
        try {
            return new String(rawData, "US-ASCII");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Developer use non-exist encoding.", e);
        }
    }

    /**
     * Converts raw byte data to ascii string.
     */
    public static String toAscii(byte[] rawData, int offset, int length) {
        try {
            return new String(rawData, offset, length, "US-ASCII");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Developer use non-exist encoding.", e);
        }
    }

    public static String toHexString(byte[] array) {
        if (null == array) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (byte b : array) {
            sb.append(String.format("%02X ", b & 0xFF));
        }
        return sb.toString();
    }
}
