package org.hejki.heklasys.msg.impl;

import org.hejki.heklasys.msg.model.MessageType;

import java.nio.ByteBuffer;

/**
 * TODO Document me.
 *
 * @author Petr Hejkal
 */
public abstract class MessageFrame {
    public static final int MESSAGE_MIN_LEN = 4;

    private final MessageType type;
    private final int identifier;

    protected MessageFrame(MessageType type, int identifier) {
        this.type = type;
        this.identifier = identifier;
    }

    public abstract byte[] getRequestData();

    public ByteBuffer createRequest() {
        byte[] requestData = getRequestData();
        ByteBuffer buffer = ByteBuffer.allocate(requestData.length + MESSAGE_MIN_LEN);

        buffer.put((byte) type.ordinal());
        buffer.put((byte) requestData.length);
        buffer.put((byte) identifier);
        buffer.put(requestData);
        buffer.put(calculateCrc(type, identifier, requestData));
        return buffer;
    }

    public static byte calculateCrc(MessageType type, int identifier, byte[] data) {
        byte checksum = (byte) (type.ordinal() + data.length + identifier);

        for (byte b : data) {
            checksum += (b & 0xFF);
        }
        return (byte) (256 - checksum);
    }

    public MessageType getType() {
        return type;
    }

    public int getIdentifier() {
        return identifier;
    }
}
