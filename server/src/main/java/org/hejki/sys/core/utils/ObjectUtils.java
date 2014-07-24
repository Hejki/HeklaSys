package org.hejki.sys.core.utils;

import org.springframework.dao.DataRetrievalFailureException;

import java.lang.reflect.Field;

/**
 * TODO Document me.
 *
 * @author Petr Hejkal
 */
public abstract class ObjectUtils {

    public static <T> void setFieldValue(Class<T> clazz, String declaredFieldName, T obj, Object value) {
        try {
            Field field = clazz.getDeclaredField(declaredFieldName);

            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception e) {
            throw new DataRetrievalFailureException("Cannot set value " + value
                + " for field " + declaredFieldName + " on object instance "
                + obj + " of type " + clazz, e);
        }
    }
}
