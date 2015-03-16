package org.hejki.heklasys.repository;

import org.hejki.heklasys.model.PinSetting;
import org.hejki.spring.data.jdbc.JdbcRepository;
import org.hejki.spring.data.jdbc.mapping.annotation.Query;

import java.util.List;

/**
* TODO Document me.
*
* @author Petr Hejkal <petr.hejkal@doxologic.com>
*/
//@Repository
//@RepositoryRestResource(collectionResourceRel = "pinSettings", path = "pinSettings")
public interface PinSettingRepository extends JdbcRepository<PinSetting, Integer> {

    @Query("select * from pin_settings where node_id = ?")
    public List<PinSetting> findByNode(Integer nodeId);

    @Query("select * from pin_settings where node_id = ? and pin_index = ?")
    public PinSetting findByNodeAndPinIndex(Integer nodeId, int index);
}
