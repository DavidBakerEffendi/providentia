package za.ac.sun.cs.providentia.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.ac.sun.cs.providentia.repository.BusinessRepository;

@Service
@Transactional
public class BusinessService {

    private final Logger log = LoggerFactory.getLogger(BusinessService.class);

    private BusinessRepository businessRepository;

    @Autowired
    public BusinessService(BusinessRepository businessRepository) {
        this.businessRepository = businessRepository;
    }
}
