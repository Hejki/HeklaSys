package org.hejki.heklasys.repository;

import org.hejki.heklasys.model.NodeValue;
import org.hejki.spring.data.jdbc.JdbcRepository;

/**
* TODO Document me.
*
* @author Petr Hejkal <petr.hejkal@doxologic.com>
*/
//@Repository
public interface NodeIndexDataRepository extends JdbcRepository<NodeValue, Long> {
}
