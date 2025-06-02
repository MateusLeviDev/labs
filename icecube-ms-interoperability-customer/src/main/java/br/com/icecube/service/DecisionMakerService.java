package br.com.icecube.service;


import br.com.icecube.domain.Decision;

import java.time.LocalDate;

public interface DecisionMakerService {
    Decision decide(String document);
}
