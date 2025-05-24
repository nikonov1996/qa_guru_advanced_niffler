package guru.qa.niffler.db;

import guru.qa.niffler.config.Config;

import static java.lang.String.format;

public enum DataSourceDB {
    AUTH("jdbc:postgresql://%s:%d/niffler-auth"),
    SPEND("jdbc:postgresql://%s:%d/niffler-spend"),
    USERDATA("jdbc:postgresql://%s:%d/niffler-userdata"),
    CURRENCY("jdbc:postgresql://%s:%d/niffler-currency");

    private final String dbSource;

    private final Config config = Config.getInstance();

    DataSourceDB(String dbSource) {
        this.dbSource = dbSource;
    }

    public String getDbSource() {
        return format(dbSource,
                config.dbHost(),
                config.dbPort());
    }

    public String getP6SpyUrl(){
        return getDbSource().replace("jdbc:","jdbc:p6spy:");
    }
}
