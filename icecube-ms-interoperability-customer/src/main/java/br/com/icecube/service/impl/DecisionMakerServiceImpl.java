package br.com.icecube.service.impl;

import br.com.icecube.domain.SSN;
import br.com.icecube.repository.DecisionRepository;
import br.com.icecube.service.DecisionMakerService;
import br.com.icecube.domain.Decision;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class DecisionMakerServiceImpl implements DecisionMakerService {

    private final DecisionRepository decisionRepository;
    @Override
    public Decision decide(Integer ssn, LocalDate birthDate) {
        Decision decision = Decision.decide(SSN.create(ssn), birthDate);
        Decision decisionCreated = decisionRepository.save(decision);
        log.info("the decision is: {}",decisionCreated);

        return decision;
    }
}
