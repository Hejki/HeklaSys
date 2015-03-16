package org.hejki.heklasys.model;

import lombok.Data;
import org.hejki.heklasys.utils.Constants;
import org.hejki.spring.data.jdbc.mapping.annotation.Table;

import javax.validation.constraints.*;

/**
 * TODO Document me.
 *
 * @author Petr Hejkal <petr.hejkal@doxologic.com>
 */
@Data
@Table(name = "nodes")
public class Node extends AbstractPersistable<Integer> {
    @NotNull
    @Size(max = 255)
    private String name;

    @NotNull
    @Pattern(regexp = Constants.IP_REGEXP)
    private String address;

    @Min(1)
    @Max(65535)
    private int port;

    /**
     * Node pin values update interval in seconds.
     */
    private int updateInterval;
}
