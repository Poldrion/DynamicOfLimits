package dynamics.model.services;

import dynamics.model.repositoies.CorrectionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CorrectionsService {

    @Autowired
    private CorrectionsRepository correctionsRepository;

}
