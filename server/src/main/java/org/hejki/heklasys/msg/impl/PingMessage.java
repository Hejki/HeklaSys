package org.hejki.heklasys.msg.impl;

import org.hejki.heklasys.msg.model.MessageType;

/**
 * TODO Document me.
 *
 * @author Petr Hejkal
 */
public class PingMessage extends MessageFrame {
    public PingMessage(int identifier) {
        super(MessageType.PING_REQUEST, identifier);
    }

    @Override
    public byte[] getRequestData() {
        return new byte[0];
    }
}
