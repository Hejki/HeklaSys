package org.hejki.heklasys.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.hejki.heklasys.utils.ByteUtils;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Constructor;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PROTECTED;
import static org.hejki.heklasys.utils.ByteUtils.appendUInt8;
import static org.hejki.heklasys.utils.ByteUtils.toUInt8;

/**
 * TODO Document me.
 * Format: [TYPE][LEN][ID][DATA...][CRC]
 *
 * @author Petr Hejkal
 */
@Slf4j
@ToString(exclude = "data")
public abstract class Message {
    protected static final int MIN_MESSAGE_SIZE = 4;

    @Getter(PACKAGE) private MessageType type;
    @Getter(PROTECTED) private int dataSize;
    @Getter @Setter private int identifier;
    private ByteBuffer data;

    public Message(MessageType type, int dataSize, int identifier) {
        this.type = type;
        this.dataSize = dataSize;
        this.identifier = identifier;
        this.data = ByteBuffer.allocate(dataSize);
    }

    public static byte calculateCrc(MessageType type, int identifier, byte[] data) {
        byte checksum = (byte) (type.ordinal() + data.length + identifier);

        for (byte b : data) {
            checksum += (b & 0xFF);
        }
        return (byte) (256 - checksum);
    }

    /**
     * Read data from stream if message was received.
     */
    protected void readData(ByteBuffer bytes) {
        throw new IllegalStateException("This message implementation was not expected any data. message_type="
                + type + ", data_size=" + dataSize);
    }

    public ByteBuffer toRaw() {
        int dataLength = getDataSize();
        ByteBuffer buffer = ByteBuffer.allocate(dataLength + MIN_MESSAGE_SIZE);
        byte[] data = dataLength > 0 ? getData().array() : new byte[0];

        buffer.order(ByteOrder.LITTLE_ENDIAN);
        appendUInt8(buffer, getType().ordinal());
        appendUInt8(buffer, dataLength);
        appendUInt8(buffer, getIdentifier());
        buffer.put(data);
        appendUInt8(buffer, calculateCrc(getType(), getIdentifier(), data));

        buffer.position(0);
        return buffer;
    }

    /**
     * Creates a message from received stream.
     */
    public static Message from(ByteBuffer buffer) {
        MessageType type = MessageType.fromByte(buffer.get());
        int size = toUInt8(buffer.get());
        int id = toUInt8(buffer.get());
        Message message = createMessage(type, size, id);

        if (buffer.remaining() != message.dataSize +1) {
            throw new IllegalArgumentException("Message data size is "
                    + (buffer.remaining() -1) + " but expected size is "
                    + message.dataSize);
        }

        if (message.dataSize > 0) {
            byte[] dataBuf = new byte[message.dataSize];

            buffer.get(dataBuf);
            message.data = ByteBuffer.wrap(dataBuf);
            message.data.order(ByteOrder.LITTLE_ENDIAN);

            if (log.isTraceEnabled()) {
                log.trace("Received data: {}", ByteUtils.toHexString(message.data.array()));
            }
            message.readData(message.getData());
        } else {
            message.data = ByteBuffer.allocate(0);
        }

        int crc = toUInt8(buffer.get());
        int calculatedCrc = toUInt8(calculateCrc(type, id, message.getData().array()));
        if (crc != calculatedCrc) {
            throw new IllegalArgumentException("Received message has invalid checksum. Value declared in message was " +
                    crc + " but calculated crc is " + calculatedCrc + ".");
        }

        log.debug("Received {}", message);
        return message;
    }

    private static Message createMessage(MessageType type, int size, int id) {
        if (null != type.getMessageClass()) {
            Constructor<? extends Message> constructor = ClassUtils.getConstructorIfAvailable(type.getMessageClass(), int.class, int.class);

            if (null != constructor) {
                try {
                    Message message = constructor.newInstance(size, id);
                    return message;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                throw new UnsupportedOperationException("Message implementation class has not required constructor with (int, int) types.");
            }
        }
        throw new IllegalArgumentException("No implementation found for message_type=" + type);
    }

    protected ByteBuffer getData() {
        data.position(0);
        return data;
    }
}
