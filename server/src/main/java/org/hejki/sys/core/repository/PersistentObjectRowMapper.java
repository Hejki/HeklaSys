package org.hejki.sys.core.repository;

import com.blogspot.nurkiewicz.jdbcrepository.RowUnmapper;
import org.hejki.sys.core.model.PersistentObject;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.StringUtils;

import java.beans.Transient;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO Document me.
 *
 * @author Petr Hejkal
 */
public class PersistentObjectRowMapper<T extends PersistentObject> implements RowMapper<T>, RowUnmapper<T> {

    /** The class we are mapping to */
    private Class<T> mappedClass;

    /** Map of the fields we provide mapping for */
    private Map<String, MappingOption> mappedFields;

    public PersistentObjectRowMapper(Class<T> mappedClass) {
        this.mappedClass = mappedClass;
        this.mappedFields = new HashMap<>();

        fillClassMappedFields(mappedClass);
    }

    private void fillClassMappedFields(Class clazz) {
        Class superclass = clazz.getSuperclass();
        if (PersistentObject.class.isAssignableFrom(superclass)) {
            fillClassMappedFields(superclass);
        }

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (isFieldMappable(field)) {
                String underscoreName = underscoreName(field.getName());
                this.mappedFields.put(underscoreName, new MappingOption(field));
            }
        }
    }

    private boolean isFieldMappable(Field field) {
        return !(field.isAnnotationPresent(org.springframework.data.annotation.Transient.class)
                || field.isAnnotationPresent(Transient.class)
                || Modifier.isTransient(field.getModifiers()));
    }

    /**
     * Convert a name in camelCase to an underscored name in lower case.
     * Any upper case letters are converted to lower case with a preceding underscore.
     * @param name the string containing original name
     * @return the converted name
     */
    private String underscoreName(String name) {
        if (!StringUtils.hasLength(name)) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        result.append(name.substring(0, 1).toLowerCase());
        for (int i = 1; i < name.length(); i++) {
            String s = name.substring(i, i + 1);
            String slc = s.toLowerCase();
            if (!s.equals(slc)) {
                result.append("_").append(slc);
            }
            else {
                result.append(s);
            }
        }
        return result.toString();
    }

    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        try {
            T obj = BeanUtils.instantiateClass(mappedClass);

            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                String column = JdbcUtils.lookupColumnName(rsmd, i);
                MappingOption mappingOption = this.mappedFields.get(column.replaceAll(" ", "").toLowerCase());

                if (null != mappingOption) {
                    try {
                        Object value = getColumnValue(rs, i, mappingOption);

                        mappingOption.setValue(obj, value);
                    } catch (Exception e) {
                        throw new DataRetrievalFailureException("Unable to map column " + column
                                + " to field " + mappingOption.fieldName + " on object of type "
                                + mappingOption.fieldDeclaringClass.getName(), e);
                    }
                }
            }

            return obj;
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, Object> mapColumns(T obj) {
        HashMap<String, Object> columns = new HashMap<>(mappedFields.size());

        for (Map.Entry<String, MappingOption> optionEntry : mappedFields.entrySet()) {
            try {
                Object value = optionEntry.getValue().getValue(obj);

                if (value != null) {
                    if (Enum.class.isAssignableFrom(value.getClass())) {
                        value = ((Enum) value).ordinal();
                    }
                    if (value instanceof LocalDateTime) {
                        Instant instant = ((LocalDateTime) value).atZone(ZoneId.systemDefault()).toInstant();
                        value = Timestamp.from(instant);
                    }
                }
                columns.put(optionEntry.getKey(), value);
            } catch (Exception e) {
                throw new RuntimeException("Unable to map field " + optionEntry.getValue().fieldName
                        + " to column " + optionEntry.getKey(), e);
            }
        }

        return columns;
    }

    /**
     * Retrieve a JDBC object value for the specified column.
     * <p>The default implementation calls
     * {@link JdbcUtils#getResultSetValue(java.sql.ResultSet, int, Class)}.
     * Subclasses may override this to check specific value types upfront,
     * or to post-process values return from {@code getResultSetValue}.
     *
     * @param rs is the ResultSet holding the data
     * @param index is the column index
     * @param mappingOption the result object mapping description
     * @return the Object value
     * @throws SQLException in case of extraction failure
     * @see org.springframework.jdbc.support.JdbcUtils#getResultSetValue(java.sql.ResultSet, int, Class)
     */
    protected Object getColumnValue(ResultSet rs, int index, MappingOption mappingOption) throws SQLException {
        if (Enum.class.isAssignableFrom(mappingOption.fieldValueType)) {
            Number enumOrdinal = (Number) rs.getObject(index);

            if (null == enumOrdinal) return null;
            return mappingOption.fieldValueType.getEnumConstants()[enumOrdinal.intValue()];
        }

        if (LocalDateTime.class.isAssignableFrom(mappingOption.fieldValueType)) {
            Timestamp timestamp = rs.getTimestamp(index);

            if (null == timestamp) return null;
            Instant instant = Instant.ofEpochMilli(timestamp.getTime());
            return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        }

        return JdbcUtils.getResultSetValue(rs, index, mappingOption.fieldValueType);
    }

    /**
     * Static factory method to create a new PersistentObjectRowMapper
     * (with the mapped class specified only once).
     *
     * @param mappedClass the class that each row should be mapped to
     */
    public static <T extends PersistentObject> PersistentObjectRowMapper<T> newInstance(Class<T> mappedClass) {
        return new PersistentObjectRowMapper<>(mappedClass);
    }

    private static class MappingOption {
        public final String fieldName;
        public final Class<?> fieldValueType;
        public final Class<?> fieldDeclaringClass;

        private MappingOption(Field field) {
            this.fieldName = field.getName();
            this.fieldValueType = field.getType();
            this.fieldDeclaringClass = field.getDeclaringClass();
        }

        public void setValue(Object obj, Object value) throws NoSuchFieldException, IllegalAccessException {
            Field field = fieldDeclaringClass.getDeclaredField(fieldName);

            field.setAccessible(true);
            field.set(obj, value);
        }

        public Object getValue(Object obj) throws IllegalAccessException, NoSuchFieldException {
            Field field = fieldDeclaringClass.getDeclaredField(fieldName);

            field.setAccessible(true);
            return field.get(obj);
        }
    }
}
