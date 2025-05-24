package guru.qa.niffler.db.jpa;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.db.DataSourceDB;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public enum EntityManagerFactoryProvider {
    INSTANCE;

    private static final Config cfg = Config.getInstance();
    private final Map<DataSourceDB, EntityManagerFactory> dataSourceStore = new ConcurrentHashMap<>();

    public EntityManagerFactory getDataSource(DataSourceDB db){
        return dataSourceStore.computeIfAbsent(db, key ->{
            Map<String, Object> props = new HashMap<>();
            props.put("hibernate.connection.url",db.getP6SpyUrl());
            props.put("hibernate.connection.user",cfg.dbUser());
            props.put("hibernate.connection.password",cfg.dbPassword());
            props.put("hibernate.connection.driver_class","com.p6spy.engine.spy.P6SpyDriver");
            props.put("hibernate.dialect","org.hibernate.dialect.PostgreSQLDialect");

            EntityManagerFactory entityManagerFactory
                    = Persistence.createEntityManagerFactory("niffler-st3",props);

            return new ThreadLocalEntityManagerFactory(entityManagerFactory);
        });
    }
}
