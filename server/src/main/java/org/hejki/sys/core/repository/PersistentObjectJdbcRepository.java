package org.hejki.sys.core.repository;

import com.blogspot.nurkiewicz.jdbcrepository.JdbcRepository;
import com.blogspot.nurkiewicz.jdbcrepository.RowUnmapper;
import com.blogspot.nurkiewicz.jdbcrepository.TableDescription;
import com.blogspot.nurkiewicz.jdbcrepository.sql.SqlGenerator;
import org.hejki.sys.core.model.PersistentObject;
import org.springframework.jdbc.core.RowMapper;

import java.lang.reflect.Field;

/**
 * TODO Document me.
 *
 * @author Petr Hejkal
 */
public abstract class PersistentObjectJdbcRepository<T extends PersistentObject> extends JdbcRepository<T, Integer> {
    public PersistentObjectJdbcRepository(RowMapper<T> rowMapper, RowUnmapper<T> rowUnmapper, SqlGenerator sqlGenerator, TableDescription table) {
        super(rowMapper, rowUnmapper, sqlGenerator, table);
    }

    public PersistentObjectJdbcRepository(RowMapper<T> rowMapper, RowUnmapper<T> rowUnmapper, TableDescription table) {
        super(rowMapper, rowUnmapper, table);
    }

    public PersistentObjectJdbcRepository(RowMapper<T> rowMapper, RowUnmapper<T> rowUnmapper, String tableName, String idColumn) {
        super(rowMapper, rowUnmapper, tableName, idColumn);
    }

    public PersistentObjectJdbcRepository(RowMapper<T> rowMapper, RowUnmapper<T> rowUnmapper, String tableName) {
        super(rowMapper, rowUnmapper, tableName);
    }

    public PersistentObjectJdbcRepository(RowMapper<T> rowMapper, TableDescription table) {
        super(rowMapper, table);
    }

    public PersistentObjectJdbcRepository(RowMapper<T> rowMapper, String tableName, String idColumn) {
        super(rowMapper, tableName, idColumn);
    }

    public PersistentObjectJdbcRepository(RowMapper<T> rowMapper, String tableName) {
        super(rowMapper, tableName);
    }

    @Override
    protected <S extends T> S postCreate(S entity, Number generatedId) {
        try {
            Field id = PersistentObject.class.getDeclaredField("id");

            id.setAccessible(true);
            id.set(entity, generatedId.intValue());
        } catch (Exception e) {
            throw new RuntimeException("Cannot process primary key for entity " + entity, e);
        }
        return entity;
    }
}
