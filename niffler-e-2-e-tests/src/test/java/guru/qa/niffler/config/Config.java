package guru.qa.niffler.config;

import org.apache.kafka.common.protocol.types.Field;

public interface Config {
    /*
    Фабричный метод:
    В зависимости от переданой при запуске системной переменной, возвращается
    нужный инстанс конфига. Который в свою очередь сделан синглтоном (экземпляр класса
    создается один раз и переиспользуется)
    */
    static Config getInstance() {
        if ("docker".equalsIgnoreCase(System.getProperty("test.env"))) {
            return DockerConfig.config;
        } else {
            return LocalConfig.config;
        }
    }

    String dbHost();

    default String dbUser() {
        return "postgres";
    }

    default String dbPassword() {
        return "secret";
    }

    default int dbPort() {
        return 5432;
    }

}
