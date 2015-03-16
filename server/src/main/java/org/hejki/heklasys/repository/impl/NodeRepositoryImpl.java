//package org.hejki.heklasys.repository.impl;
//
//import com.nurkiewicz.jdbcrepository.JdbcRepository;
//import com.nurkiewicz.jdbcrepository.RowUnmapper;
//import org.hejki.heklasys.model.Node;
//import org.hejki.heklasys.repository.NodeRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.rest.core.annotation.RestResource;
//import org.springframework.jdbc.core.BeanPropertyRowMapper;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.RowMapper;
//import org.springframework.stereotype.Repository;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * TODO Document me.
// *
// * @author Petr Hejkal <petr.hejkal@doxologic.com>
// */
//@Repository
//public class NodeRepositoryImpl extends JdbcRepository<Node, Integer> implements NodeRepository {
//    private static final RowMapper<Node> ROW_MAPPER = new BeanPropertyRowMapper<Node>(Node.class);
//    private static final RowUnmapper<Node> ROW_UNMAPPER = node -> {
//        Map<String, Object> map = new HashMap<>();
//
//        map.put("id", node.getId());
//        map.put("name", node.getName());
//        map.put("address", node.getAddress());
//        map.put("port", node.getPort());
//        map.put("update_interval", node.getUpdateInterval());
//        return map;
//    };
//
//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//
//    public NodeRepositoryImpl() {
//        super(ROW_MAPPER, ROW_UNMAPPER, "Nodes");
//    }
//
//    public Node findByAddress(String address) {
//        return jdbcTemplate.queryForObject("select * from Nodes where address = ?", ROW_MAPPER, address);
//    }
//}
