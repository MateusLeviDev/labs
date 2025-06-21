package br.com.icecube.domain.model;

import br.com.icecube.domain.enums.State;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class Decision {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    private State state;

    private Document document;

    private Decision(State state, Document document) {
        this.state = state;
        this.document = document;
    }

    public static Decision decide(Document document) {

        if (document.getValue().startsWith("0")) {
            return new Decision(State.REJECTED, document);
        } else if (document.getValue().startsWith("4")) {
            return new Decision(State.APPROVED, document);
        }
        return new Decision(State.PRE_APPROVED, document);
    }

}
