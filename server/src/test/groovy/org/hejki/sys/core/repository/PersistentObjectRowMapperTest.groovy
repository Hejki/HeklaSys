package org.hejki.sys.core.repository
import org.hejki.sys.core.model.PersistentObject
import spock.lang.Specification

import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.Timestamp

class PersistentObjectRowMapperTest extends Specification {

    def "map"() {
        A result;
        def mapper = new PersistentObjectRowMapper(A)
        def rs = Mock(ResultSet)
        def rsmd = Mock(ResultSetMetaData)

        when:
        result = mapper.mapRow(rs, 1)

        then:
        rsmd.getColumnCount() >> 4
        rsmd.getColumnLabel(1) >> "id"
        rsmd.getColumnLabel(2) >> "name"
        rsmd.getColumnLabel(3) >> "create_date"
        rsmd.getColumnLabel(4) >> "enum_value"
        1 * rs.getMetaData() >> rsmd

        1 * rs.getInt(1) >> 21
        1 * rs.getString(2) >> 'Pepa'
        1 * rs.getTimestamp(3) >> new Timestamp(10432)
        1 * rs.getObject(4) >> 1
        null != result
        21 == result.id
        'Pepa' == result.name
        10432 == result.createDate.time
        AE.TWO == result.enumValue
    }

    def "unmap"() {
        def result
        A obj = new A()
        obj.name = 'Pepino'
        obj.createDate = new Date(49986)
        obj.enumValue = AE.TWO

        def field = obj.class.superclass.getDeclaredField("id")
        field.accessible = true
        field.set(obj, 23)

        when:
        result = PersistentObjectRowMapper.newInstance(A).mapColumns(obj)

        then:
        23 == result['id']
        'Pepino' == result['name']
        49986 == result['create_date'].time
        1 == result['enum_value']
    }

    public static class A extends PersistentObject {
        private AE enumValue;
        private String name
        private Date createDate

        private A() {}
    }

    public enum AE {
        ONE, TWO;
    }
}
