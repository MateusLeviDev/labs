package br.com.icecube.domain.repository;

import br.com.icecube.domain.model.Decision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DecisionRepository extends JpaRepository<Decision,Long> {
}
