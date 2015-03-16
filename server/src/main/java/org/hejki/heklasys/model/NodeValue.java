package org.hejki.heklasys.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * TODO Document me.
 *
 * @author Petr Hejkal <petr.hejkal@doxologic.com>
 */
@Getter
@ToString
public class NodeValue extends AbstractPersistable<Long> {

    @NonNull
    private final int nodeId;

    @NonNull
//    @Field(type = FieldType.Ip)
    private final String nodeAddress;

    @NonNull
    private final PinSettingType type;

    @NonNull
    private final double value;

    @NonNull
//    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private final String timestamp;

    public NodeValue(Node node, PinSetting settings, int pinValue) {
        this.nodeId = node.getId();
        this.nodeAddress = node.getAddress();
        this.type = settings.getType();
        this.value = (double) pinValue / 100;
        this.timestamp = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(ZonedDateTime.now());
    }
}
