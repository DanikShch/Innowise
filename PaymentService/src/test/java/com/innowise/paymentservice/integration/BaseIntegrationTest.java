package com.innowise.paymentservice.integration;

import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tag("integration")
public abstract class BaseIntegrationTest {

    @Container
    static final MongoDBContainer mongo =
            new MongoDBContainer("mongo:7.0");

    @Container
    static final KafkaContainer kafka =
            new KafkaContainer(
                    DockerImageName
                            .parse("confluentinc/cp-kafka:7.6.0")
                            .asCompatibleSubstituteFor("confluentinc/cp-kafka")
            );

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {

        registry.add(
                "spring.data.mongodb.uri",
                mongo::getReplicaSetUrl
        );

        registry.add(
                "spring.kafka.bootstrap-servers",
                kafka::getBootstrapServers
        );

        registry.add(
                "app.jwt.secret",
                () -> "testsecret123456testsecret123456testsecret123456testsecret123456"
        );

        registry.add(
                "external.random-api.url",
                () -> "http://localhost:8089/random"
        );
    }
}
