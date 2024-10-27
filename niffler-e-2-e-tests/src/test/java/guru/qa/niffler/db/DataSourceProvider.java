package guru.qa.niffler.db;

import guru.qa.niffler.config.Config;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum DataSourceProvider {

    // dataSource должен быть синглтоном, чтобы не плодить подключения
    // и не занимать ресурсы, иначе будут отваливаться коннекты с бд в рантайме

    INSTANCE; // singleton realisation

    private static final Config config = Config.getInstance();

    private final Map<DataSourceDB,PGSimpleDataSource> dbSources = new ConcurrentHashMap<>();

    public DataSource getDataSource(DataSourceDB dataSource) {
        // метод computeIfAbsent возвращает значение из мапы по ключу если оно там есть,
        // если значение отсутсвует, то метод запишет его в мапу и вернет.
        // Таким образом объект PGSimpleDataSource для каждой базы данных создастся один раз,
        // а далее будет просто возвращаться по ключу
        return dbSources.computeIfAbsent(dataSource, ds ->{
            PGSimpleDataSource pg = new PGSimpleDataSource();
            pg.setURL(ds.getDbSource());
            pg.setUser(config.dbUser());
            pg.setPassword(config.dbPassword());
            return pg;
        });
    }


}
