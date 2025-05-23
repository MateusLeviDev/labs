#### Projetcs
- core-customer: Event Routing com Event-Type Header Pattern dentro de uma arquitetura Event-Driven baseada em Pub/Sub. Spring Cloud Stream com Kafka e programação reativa


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

#### Configurações do Produtor

- MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION : Se estivermos enviando várias mensagens, esta configuração no Kafka ajuda a decidir quantas mensagens podemos enviar sem esperar por uma confirmação de "leitura". Se definirmos um valor maior que 1 sem ativar a idempotência, podemos acabar alterando a ordem das nossas mensagens se precisarmos reenviá-las. Mas, se ativarmos a idempotência, o Kafka mantém as mensagens em ordem, mesmo se enviarmos várias de uma vez. Para uma ordem super estrita, como garantir que cada mensagem seja lida antes do envio da próxima, devemos definir este valor como 1. Se quisermos priorizar a velocidade em vez da ordem perfeita, podemos definir até 5, mas isso pode potencialmente introduzir problemas de ordenação.


- BATCH_SIZE_CONFIG e LINGER_MS_CONFIG : O Kafka controla o tamanho padrão do lote em bytes, visando agrupar registros para a mesma partição em menos solicitações para melhor desempenho. Se definirmos esse limite muito baixo, enviaremos muitos grupos pequenos, o que pode nos atrasar. Mas se o definirmos muito alto, pode não ser o melhor uso da nossa memória. O Kafka pode esperar um pouco antes de enviar um grupo se ele ainda não estiver cheio. Esse tempo de espera é controlado por LINGER_MS_CONFIG. Se mais mensagens chegarem com rapidez suficiente para preencher nosso limite definido, elas serão enviadas imediatamente, mas se não, o Kafka não fica esperando – ele envia o que tivermos quando o tempo acabar. É como equilibrar velocidade e eficiência, garantindo que estamos enviando apenas mensagens suficientes por vez, sem atrasos desnecessários.


---


```exemplo .properties

server.port=8082

spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=create
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

spring.datasource.url=jdbc:postgresql://localhost:5433/decision_db
spring.datasource.username=postgres
spring.datasource.password=postgres

spring.cloud.function.definition=processCustomerCreated;processCustomerCreatedFromRetryTopic

#----- configurations for original topic
spring.cloud.stream.bindings.processCustomerCreated-in-0.destination=customer-created-topic
spring.cloud.stream.bindings.processCustomerCreated-in-0.group=decision-microservice
spring.cloud.stream.bindings.processCustomerCreated-in-0.consumer.max-attempts=0
spring.cloud.stream.bindings.processCustomerCreated-in-0.consumer.concurrency=3
spring.cloud.stream.kafka.bindings.processCustomerCreated-in-0.consumer.enable-dlq=true
spring.cloud.stream.kafka.bindings.processCustomerCreated-in-0.consumer.dlq-name=decision.customer-retry-topic

#----- configurations for retry topic
spring.cloud.stream.bindings.processCustomerCreatedFromRetryTopic-in-0.destination=decision.customer-retry-topic
spring.cloud.stream.bindings.processCustomerCreatedFromRetryTopic-in-0.consumer.concurrency=3
spring.cloud.stream.bindings.processCustomerCreatedFromRetryTopic-in-0.consumer.max-attempts=3
spring.cloud.stream.bindings.processCustomerCreatedFromRetryTopic-in-0.consumer.retryable-exceptions.br.com.icecube.exception.TransientFailureException=true
spring.cloud.stream.bindings.processCustomerCreatedFromRetryTopic-in-0.consumer.back-off-initial-interval=1000
spring.cloud.stream.bindings.processCustomerCreatedFromRetryTopic-in-0.consumer.back-off-max-interval=10000
spring.cloud.stream.bindings.processCustomerCreatedFromRetryTopic-in-0.consumer.back-off-multiplier=2.0


spring.cloud.stream.kafka.bindings.processCustomerCreatedFromRetryTopic-in-0.consumer.enable-dlq=true
spring.cloud.stream.kafka.bindings.processCustomerCreatedFromRetryTopic-in-0.consumer.dlq-name=decision.customer-DLQ
spring.cloud.stream.bindings.processCustomerCreatedFromRetryTopic-in-0.group=decision-microservice



```

