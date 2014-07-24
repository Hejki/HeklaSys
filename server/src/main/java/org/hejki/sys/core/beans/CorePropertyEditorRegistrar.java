package org.hejki.sys.core.beans;

import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;

import java.net.InetAddress;

/**
 * TODO Document me.
 *
 * @author Petr Hejkal
 */
public class CorePropertyEditorRegistrar implements PropertyEditorRegistrar {
    @Override
    public void registerCustomEditors(PropertyEditorRegistry registry) {
        registry.registerCustomEditor(InetAddress.class, new InetAddressPropertyEditor());
    }
}
