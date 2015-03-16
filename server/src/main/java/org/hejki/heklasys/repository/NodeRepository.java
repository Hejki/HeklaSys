package org.hejki.heklasys.repository;

import org.hejki.heklasys.model.Node;
import org.hejki.spring.data.jdbc.JdbcRepository;
import org.hejki.spring.data.jdbc.mapping.annotation.Query;

/**
 * TODO Document me.
 *
 * @author Petr Hejkal <petr.hejkal@doxologic.com>
 */
//@RepositoryRestResource(path = "nodes", collectionResourceRel = "nodes")
public interface NodeRepository extends JdbcRepository<Node, Integer> {

    @Query("select * from nodes where address = ?")
    public Node findByAddress(String address);

    @Query(value = "insert into temperatures(node_id, value) values(?, ?)", update = true)
    void storeTemperature(int nodeId, double value);
}
