package org.hejki.heklasys.portal.model;

import org.hejki.heklasys.msg.model.MessageType;

import java.io.Serializable;

/**
 * TODO Document me.
 *
 * @author Petr Hejkal
 */
public class MessageDto implements Serializable {
    private static int idIterator;

    private MessageType type;
    private String ip = "10.0.0.101";
    private int identifier = idIterator++;
    private String data;

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
