package guru.qa.niffler.config;

public class DockerConfig implements Config {

    static final Config config = new DockerConfig();

    private DockerConfig(){

    }

    @Override
    public String dbHost() {
        return "niffler-all-db";
    }
}
