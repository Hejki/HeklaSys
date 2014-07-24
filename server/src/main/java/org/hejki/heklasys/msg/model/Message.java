package org.hejki.heklasys.msg.model;

import org.hejki.heklasys.core.model.devices.NodeDevice;
import org.hejki.heklasys.msg.impl.MessageFrame;
import org.hejki.sys.core.model.PersistentObject;
import org.hejki.sys.core.utils.ByteUtils;
import org.springframework.data.annotation.Transient;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * TODO Document me.
 *
 * @author Petr Hejkal
 */
public class Message extends PersistentObject {
    private MessageType type;
    private int identifier;
    private byte[] requestData;
    private LocalDateTime requestSend;

    private LocalDateTime responseReceive;
    private MessageType responseType;
    private byte[] responseData;

    @Transient
    private NodeDevice node;
    private Integer nodeId;

    public Message() {}

    public static Message messageFromFrame(MessageFrame frame, NodeDevice node) {
        Message message = new Message();

        message.type = frame.getType();
        message.identifier = frame.getIdentifier();
        message.requestData = frame.getRequestData();
        message.requestSend = LocalDateTime.now();
        message.node = node;
        message.nodeId = node.getId();
        return message;
    }

    public MessageType getType() {
        return type;
    }

    public int getIdentifier() {
        return identifier;
    }

    public byte[] getRequestData() {
        return requestData;
    }

    public String getRequestDataStr() {
        return ByteUtils.toHexString(requestData);
    }

    public LocalDateTime getRequestSend() {
        return requestSend;
    }

    public LocalDateTime getResponseReceive() {
        return responseReceive;
    }

    public void setResponseReceive(LocalDateTime responseReceive) {
        this.responseReceive = responseReceive;
    }

    public MessageType getResponseType() {
        return responseType;
    }

    public void setResponseType(MessageType responseType) {
        this.responseType = responseType;
    }

    public byte[] getResponseData() {
        return responseData;
    }

    public void setResponseData(byte[] responseData) {
        this.responseData = responseData;
    }

    public String getResponseDataStr() {
        return ByteUtils.toHexString(responseData);
    }

    public NodeDevice getNode() {
        return node;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Message{");
        sb.append("type=").append(type);
        sb.append(", identifier=").append(identifier);
        sb.append(", requestData=").append(Arrays.toString(requestData));
        sb.append(", requestSend=").append(requestSend);
        sb.append(", responseReceive=").append(responseReceive);
        sb.append(", responseType=").append(responseType);
        sb.append(", responseData=").append(Arrays.toString(responseData));
        sb.append(", node=").append(node);
        sb.append(", nodeId=").append(nodeId);
        sb.append('}');
        return sb.toString();
    }
}
