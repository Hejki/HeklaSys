package org.hejki.heklasys.utils;

/**
 * TODO Document me.
 *
 * @author Petr Hejkal <petr.hejkal@doxologic.com>
 */
public class Constants {
    private static final String IP_REGEXP_PART = "([01]?\\d\\d?|2[0-4]\\d|25[0-5])";
    public static final String IP_REGEXP = "^" + IP_REGEXP_PART + "\\." + IP_REGEXP_PART + "\\."
            + IP_REGEXP_PART + "\\." + IP_REGEXP_PART + "$";

    private Constants() {}
}
