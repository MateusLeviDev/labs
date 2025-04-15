#### Start Broker

`docker run -d -p 9092:9092 --name broker apache/kafka:3.8.0`


```

@Bean
public Sinks.Many<CustomerEvent> customerCreatedEventStream() {
    return Sinks.many().replay().latest();
}



Sinks.Many<CustomerEvent>: sink emissor de eventos reativos p/ mult subscribers
replay().latest():  último evento emitido. Quando novos assinantes se conectam, 
eles recebem o evento mais recente.

em suma: bean responsável por gerenciar o stream dos eventos do 
tipo CustomerEvent.

```
---

```

@Bean
public Supplier<Flux<CustomerEvent>> customerEventSink() {
    return () -> customerCreatedEventStream().asFlux();
}



Supplier<Flux<CustomerEvent>>: Um bean funcional que fornece o stream reativo 
(Flux) dos eventos.

customerCreatedEventStream().asFlux(): Converte o Sink em um Flux, permitindo a 
transmissão reativa dos eventos para consumidores.

em suma: Esse bean é uma função Spring Cloud Stream, usada para publicar eventos 
no tópico Kafka configurado.

```

- Um fluxo reativo é um modelo de programação assíncrona e orientada a eventos. Baseado no príncipio de backpressure
<br> que permite que assinantes consumam eventos em seu próprio ritmo, evitando sobrecarga
- para sistemas que precisam processar muitos eventos simultaneamente.
-  processamento de streams em tempo real, integração de sistemas

---

```

spring.cloud:
  function.definition: customerEventSink
  stream.bindings:
    customerEventSink-out-0.destination: customer-created-topic


function.definition: Define bean funcional que vai produzir os eventos
stream.bindings: Configura o topic no qual os eventos emitidos pelo 
customerEventSink serão enviados

```

- `out`: indica que é um output do app. ou seja, indica que dados estão sendo enviados do serviço para um destino externo
- neste caso, ele vai pegar a porta 9092 por default
- `-0`: Representa o índice de uma saída específica em caso de múltiplas saídas.
- No caso de uma única saída, o índice -0 é o padrão. Para múltiplas saídas, índices incrementais (out-1, out-2, etc.) 
<br> são usados para identificar e configurar separadamente cada saída.

``` 
exemplo mult saídas


spring.cloud.stream.bindings:
  customerEventSink-out-0.destination: customer-created-topic
  orderEventSink-out-1.destination: order-created-topic


```

- `in`: epresenta um ponto em que a aplicação recebe mensagens.


#### Idempotent Producer

Pattern to imporve ms message delivery.

Transient Failures: are temporary issues that can resolve on their own without manual intervation. For producer this includes network glitches, broker unavailability, timeout errors etc

How do we solve duplication issue? (even if retries occur due to network issues or other failures)
precisamos habilitar o idempotent. dessa forma, o kafka assigns a producre ID (PID) and sequence numbers to messages
PID + SEQ

- kafka broker assigns PID when the producer first contact. ou seja, mesmo se enviar o ack e falhar, será gravado. dessa forma permite saber as duplicatas

use case 1 Duplicated message solved w/ idempotent producer
- ![Screenshot from 2025-04-15 16-38-38](https://github.com/user-attachments/assets/255f6941-13f6-45f6-9ca5-6f4b4c164a8f)
