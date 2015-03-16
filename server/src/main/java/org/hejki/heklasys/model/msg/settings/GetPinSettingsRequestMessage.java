package org.hejki.heklasys.model.msg.settings;

import lombok.Value;
import org.hejki.heklasys.model.PinSettingType;
import org.hejki.heklasys.model.RequestMessage;
import org.hejki.heklasys.model.msg.response.OkResponseMessage;

import java.nio.ByteBuffer;

import static org.hejki.heklasys.model.MessageType.GET_PIN_SETTINGS;
import static org.hejki.heklasys.utils.ByteUtils.appendUInt8;
import static org.hejki.heklasys.utils.ByteUtils.toUInt8;

/**
 * TODO Document me.
 *
 * @author Petr Hejkal <petr.hejkal@doxologic.com>
 */
public class GetPinSettingsRequestMessage extends RequestMessage<GetPinSettingsRequestMessage.Response> {
    @Value
    public static class Response {
        private int pinIndex;
        private int pinNumber;
        private PinSettingType type;
        private int config;
    }

    public GetPinSettingsRequestMessage(int pinIndex) {
        super(GET_PIN_SETTINGS, 1);
        appendUInt8(getData(), pinIndex);
    }

    @Override
    public Response parseOkResponse(OkResponseMessage message) {
        ByteBuffer data = message.getData();

        return new Response(
                toUInt8(getData()), // index
                toUInt8(data), // num
                PinSettingType.values()[toUInt8(data)], // type
                toUInt8(data) // config
        );
    }
}
