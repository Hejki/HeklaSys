package org.hejki.heklasys.model;

/**
 * TODO Document me.
 *
 * @author Petr Hejkal
 */
public abstract class NoDataMessage extends Message {
    public NoDataMessage(MessageType type, int dataSize, int identifier) {
        super(type, dataSize, identifier);
        //TODO check if dataSize is 0
    }
}
