package br.com.icecube.domain.service;


import br.com.icecube.domain.model.Decision;

public interface DecisionMakerService {
    Decision decide(String document);
}
