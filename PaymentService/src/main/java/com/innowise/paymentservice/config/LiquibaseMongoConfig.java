package com.innowise.paymentservice.config;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LiquibaseMongoConfig {

    @Bean
    public Liquibase liquibase() throws LiquibaseException {

        Database database = DatabaseFactory.getInstance()
                .openDatabase(
                        "mongodb://localhost:27017/payment_db",
                        null,
                        null,
                        null,
                        new ClassLoaderResourceAccessor()
                );

        Liquibase liquibase = new Liquibase(
                "db/changelog/db.changelog-master.yaml",
                new ClassLoaderResourceAccessor(),
                database
        );

        liquibase.update(new Contexts(), new LabelExpression());
        return liquibase;
    }
}
