package za.ac.sun.cs.providentia.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.ac.sun.cs.providentia.repository.StateRepository;

@Service
@Transactional
public class StateService {

    private final Logger log = LoggerFactory.getLogger(StateService.class);

    private StateRepository stateRepository;

    @Autowired
    public StateService(StateRepository stateRepository) {
        this.stateRepository = stateRepository;
    }
}
