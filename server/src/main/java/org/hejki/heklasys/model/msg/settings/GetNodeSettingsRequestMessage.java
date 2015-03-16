package org.hejki.heklasys.model.msg.settings;

import lombok.Value;
import org.hejki.heklasys.model.RequestMessage;
import org.hejki.heklasys.model.msg.response.OkResponseMessage;

import java.nio.ByteBuffer;

import static org.hejki.heklasys.model.MessageType.GET_NODE_SETTINGS;
import static org.hejki.heklasys.utils.ByteUtils.*;

/**
 * TODO Document me.
 *
 * @author Petr Hejkal <petr.hejkal@doxologic.com>
 */
public class GetNodeSettingsRequestMessage extends RequestMessage<GetNodeSettingsRequestMessage.Response> {
    public enum TimeUnit {
        SECONDS, MINUTES, HOURS
    }

    @Value
    public static class Response {
        private int sendingInterval;
        private TimeUnit sendingIntervalUnit;
        private int numberOfPinPositions;
        private String serverIp;
        private int serverPort;
    }

    public GetNodeSettingsRequestMessage() {
        super(GET_NODE_SETTINGS, 0);
    }

    @Override
    public Response parseOkResponse(OkResponseMessage message) {
        ByteBuffer data = message.getData();

        return new Response(
                toUInt8(data), // interval
                TimeUnit.values()[toUInt8(data)], // interval unit
                toUInt8(data), // pin positions
                toIPv4(data), // server ip
                toUInt16(data) // server port
        );
    }
}
