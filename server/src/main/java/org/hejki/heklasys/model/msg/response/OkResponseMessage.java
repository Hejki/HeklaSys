package org.hejki.heklasys.model.msg.response;

import lombok.extern.slf4j.Slf4j;
import org.hejki.heklasys.model.MessageType;
import org.hejki.heklasys.utils.ByteUtils;

import java.nio.ByteBuffer;

/**
 * TODO Document me.
 *
 * @author Petr Hejkal
 */
@Slf4j
public class OkResponseMessage extends ResponseMessage {

    public OkResponseMessage(int dataSize, int identifier) {
        super(MessageType.RESPONSE_OK, dataSize, identifier);
    }

    @Override
    protected void readData(ByteBuffer bytes) {
        if (log.isDebugEnabled()) {
            log.debug("OK message data_length={}, data={}",
                    bytes.limit(),
                    ByteUtils.toHexString(bytes.array()));
        }
        // do nothing
    }
}
