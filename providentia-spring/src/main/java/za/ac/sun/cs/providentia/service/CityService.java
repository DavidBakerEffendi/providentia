package za.ac.sun.cs.providentia.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.ac.sun.cs.providentia.repository.CityRepository;

@Service
@Transactional
public class CityService {

    private final Logger log = LoggerFactory.getLogger(CityService.class);

    private CityRepository cityRepository;

    @Autowired
    public CityService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }
}
