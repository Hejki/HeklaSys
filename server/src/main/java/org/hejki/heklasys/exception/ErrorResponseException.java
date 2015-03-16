package org.hejki.heklasys.exception;

import org.hejki.heklasys.model.msg.response.ErrorResponseMessage;

/**
 * TODO Document me.
 *
 * @author Petr Hejkal <petr.hejkal@doxologic.com>
 */
public class ErrorResponseException extends RuntimeException {
    public ErrorResponseException(ErrorResponseMessage.ErrorCode errorCode) {
        super(errorCode.name());
    }
}
