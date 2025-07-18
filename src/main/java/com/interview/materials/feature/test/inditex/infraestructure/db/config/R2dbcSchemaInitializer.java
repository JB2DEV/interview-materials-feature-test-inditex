package com.interview.materials.feature.test.inditex.infraestructure.db.config;

import io.r2dbc.spi.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;

@Slf4j
@Configuration
public class R2dbcSchemaInitializer {

    @Bean
    public ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {
        log.info("🛠️  Inicializando base de datos con schema.sql...");

        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);

        try {
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator(new ClassPathResource("schema.sql"));
            initializer.setDatabasePopulator(populator);
            log.info("✅ schema.sql configurado correctamente para inicialización.");
        } catch (Exception e) {
            log.error("❌ Error al configurar el schema.sql para inicialización de la base de datos", e);
        }

        return initializer;
    }
}