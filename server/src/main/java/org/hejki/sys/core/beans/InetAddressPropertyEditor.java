package org.hejki.sys.core.beans;

import java.beans.PropertyEditorSupport;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * TODO Document me.
 *
 * @author Petr Hejkal
 */
public class InetAddressPropertyEditor extends PropertyEditorSupport {


    @Override
    public String getAsText() {
        return super.getAsText();
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        try {
            InetAddress value = InetAddress.getByName(text);
            setValue(value);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
