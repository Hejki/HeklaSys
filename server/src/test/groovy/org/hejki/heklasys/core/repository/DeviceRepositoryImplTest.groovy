package org.hejki.heklasys.core.repository
import org.hejki.heklasys.TestConfiguration
import org.hejki.heklasys.core.model.Device
import org.hejki.heklasys.core.model.devices.NodeDevice
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration(classes = [DeviceRepositoryImplTest, TestConfiguration])
class DeviceRepositoryImplTest extends Specification {
    @Autowired
    DeviceRepository repository

    @Autowired
    JdbcTemplate template

    def "findAll"() {
        Page<Device> result
        def sort = new Sort(Sort.Direction.ASC, "identifier")

        when:
        result = repository.findAll(new PageRequest(0, 10, sort))

        then:
        3 == result.numberOfElements
        2000 == result.content[0].id
        1000 == result.content[1].id
        'Hejkino B' == result.content[0].identifier
        null == result.content[0].node
        1001 == result.content[0].room.id
        'Loznice' == result.content[0].room.name
    }

    def "findOne"() {
        NodeDevice obj

        when:
        obj = repository.findOne(1000)

        then:
        NodeDevice == obj.class
        1000 == obj.id
        'Hejkino L' == obj.identifier
        [10,0,0,100] as byte[] == obj.ip.address
        2210 == obj.port

    }

    @Bean
    DeviceRepository deviceRepository() {
        return new DeviceRepositoryImpl()
    }
}
