package org.hejki.heklasys.model.msg.response;

import lombok.Getter;
import lombok.ToString;
import org.hejki.heklasys.model.Message;
import org.hejki.heklasys.model.MessageType;

import java.nio.ByteBuffer;

/**
 * TODO Document me.
 *
 * @author Petr Hejkal <petr.hejkal@doxologic.com>
 */
@Getter
@ToString(callSuper = true)
public class PinValueMessage extends Message {
    private int pinIndex;
    private int pinValue;

    public PinValueMessage(int dataSize, int identifier) {
        super(MessageType.PIN_VALUE, dataSize, identifier);
    }

    @Override
    protected void readData(ByteBuffer bytes) {
        pinIndex = bytes.get() & 0xFF;
        pinValue = bytes.getShort() & 0xFFFF;
    }
}
