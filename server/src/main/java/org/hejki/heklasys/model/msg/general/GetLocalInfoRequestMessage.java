package org.hejki.heklasys.model.msg.general;

import lombok.Value;
import org.hejki.heklasys.model.RequestMessage;
import org.hejki.heklasys.model.msg.response.OkResponseMessage;

import java.nio.ByteBuffer;

import static org.hejki.heklasys.model.MessageType.GET_LOCAL_INFO;
import static org.hejki.heklasys.utils.ByteUtils.toIPv4;
import static org.hejki.heklasys.utils.ByteUtils.toUInt16;

/**
 * TODO Document me.
 *
 * @author Petr Hejkal <petr.hejkal@doxologic.com>
 */
public class GetLocalInfoRequestMessage extends RequestMessage<GetLocalInfoRequestMessage.Response> {
    @Value
    public static class Response {
        private String address;
        private int port;
    }

    public GetLocalInfoRequestMessage() {
        super(GET_LOCAL_INFO, 0);
    }

    @Override
    public Response parseOkResponse(OkResponseMessage message) {
        ByteBuffer data = message.getData();

        return new Response(toIPv4(data), toUInt16(data));
    }
}
