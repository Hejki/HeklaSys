package org.hejki.heklasys.msg.service;

import org.hejki.heklasys.core.model.devices.NodeDevice;
import org.hejki.heklasys.msg.impl.MessageFrame;

import java.net.SocketAddress;
import java.nio.ByteBuffer;

/**
 * TODO Document me.
 *
 * @author Petr Hejkal
 */
public interface MessageCommunicationService {

    void send(MessageFrame message, NodeDevice target);

    void receive(SocketAddress source, ByteBuffer message);
}
