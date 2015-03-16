package org.hejki.heklasys.model;

import lombok.Data;
import org.hejki.spring.data.jdbc.mapping.annotation.Column;
import org.hejki.spring.data.jdbc.mapping.annotation.Table;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * TODO Document me.
 *
 * @author Petr Hejkal <petr.hejkal@doxologic.com>
 */
@Data
@Table(name = "pin_settings")
//@CompoundIndex(unique = true, useGeneratedName = true, def = "{'node': 1, 'pinIndex': 1}")
public class PinSetting extends AbstractPersistable<Integer> {
    public static final int MAX_SETTINGS_COUNT = 32;

    //    @DBRef
    @NotNull
    @Column(name = "node_id")
    private Node node;

    @Min(0) @Max(31)
    private int pinIndex;

    @Min(0) @Max(255)
    private int pinNumber;

    @NotNull
    private PinSettingType type;

    @Min(0) @Max(255)
    private int configuration;
}
