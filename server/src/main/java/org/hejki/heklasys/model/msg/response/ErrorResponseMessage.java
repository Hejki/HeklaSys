package org.hejki.heklasys.model.msg.response;

import lombok.Getter;
import lombok.ToString;
import org.hejki.heklasys.model.MessageType;

import java.nio.ByteBuffer;

/**
 * TODO Document me.
 *
 * @author Petr Hejkal
 */
@ToString(callSuper = true)
public class ErrorResponseMessage extends ResponseMessage {
    @Getter private ErrorCode error;

    public ErrorResponseMessage(int dataSize, int identifier) {
        super(MessageType.RESPONSE_NOK, dataSize, identifier);
    }

    @Override
    protected void readData(ByteBuffer bytes) {
        error = ErrorCode.values()[bytes.get()];
    }

    public enum ErrorCode {
        UNSUPPORTED_MSG,
        INVALID_CHECKSUM,
        NOT_IMPLEMENTED_MSG,
        MSG_INCORRECT_IMPL,
        BAD_MSG_LENGTH,
        NOT_SETTING_MODE,
        IN_SETTING_MODE,
    }
}
