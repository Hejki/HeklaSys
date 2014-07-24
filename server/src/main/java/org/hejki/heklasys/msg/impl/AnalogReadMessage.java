package org.hejki.heklasys.msg.impl;

import org.hejki.heklasys.msg.model.MessageType;

/**
 * TODO Document me.
 *
 * @author Petr Hejkal
 */
public class AnalogReadMessage extends MessageFrame {
    private final AnalogPin pin;

    public AnalogReadMessage(int identifier, AnalogPin pin) {
        super(MessageType.ANALOG_READ, identifier);
        this.pin = pin;
    }

    @Override
    public byte[] getRequestData() {
        return new byte[] {pin.number};
    }

    public enum AnalogPin {
        A0(14), A1(15), A2(16), A3(17), A4(18), A5(19), A6(20), A7(21);

        private byte number;
        AnalogPin(int number) {
            this.number = (byte) number;
        }
    }
}
