package org.hejki.heklasys.model;

import lombok.ToString;
import org.hejki.heklasys.model.msg.response.OkResponseMessage;

/**
 * TODO Document me.
 *
 * @author Petr Hejkal <petr.hejkal@doxologic.com>
 */
@ToString(callSuper = true)
public abstract class RequestMessage<T> extends Message {
    public RequestMessage(MessageType type, int dataSize) {
        super(type, dataSize, 0);
    }

    public abstract T parseOkResponse(OkResponseMessage message);
}
