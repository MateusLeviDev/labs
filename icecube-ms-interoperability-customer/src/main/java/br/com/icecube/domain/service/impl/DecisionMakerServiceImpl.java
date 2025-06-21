package br.com.icecube.domain.service.impl;

import br.com.icecube.domain.model.Document;
import br.com.icecube.domain.repository.DecisionRepository;
import br.com.icecube.domain.service.DecisionMakerService;
import br.com.icecube.domain.model.Decision;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DecisionMakerServiceImpl implements DecisionMakerService {

    private final DecisionRepository decisionRepository;

    @Override
    public Decision decide(String document) {
        Decision decision = Decision.decide(Document.of(document));
        Decision decisionCreated = decisionRepository.save(decision);
        log.info("the decision is: {}",decisionCreated);

        return decision;
    }
}
