package za.ac.sun.cs.providentia.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cassandra")
public class CassandraController {

    private final Logger log = LoggerFactory.getLogger(CassandraController.class);

    @Autowired
    public CassandraController() {

    }

}
