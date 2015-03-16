package org.hejki.heklasys.model.msg.response;

import org.hejki.heklasys.model.Message;
import org.hejki.heklasys.model.MessageType;

import java.nio.ByteBuffer;

/**
 * TODO Document me.
 *
 * @author Petr Hejkal <petr.hejkal@doxologic.com>
 */
public abstract class ResponseMessage extends Message {
    public ResponseMessage(MessageType type, int dataSize, int identifier) {
        super(type, dataSize, identifier);
    }

    @Override
    public ByteBuffer getData() {
        return super.getData();
    }
}
