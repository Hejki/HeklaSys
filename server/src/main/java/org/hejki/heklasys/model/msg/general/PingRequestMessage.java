package org.hejki.heklasys.model.msg.general;

import org.hejki.heklasys.model.MessageType;
import org.hejki.heklasys.model.NoDataMessage;

/**
 * TODO Document me.
 *
 * @author Petr Hejkal
 */
public class PingRequestMessage extends NoDataMessage {
    public PingRequestMessage(int dataSize, int identifier) {
        super(MessageType.PING_REQUEST, dataSize, identifier);
    }
}
