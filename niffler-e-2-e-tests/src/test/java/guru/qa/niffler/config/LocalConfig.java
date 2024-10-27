package guru.qa.niffler.config;

public class LocalConfig implements Config{

    // модификатор доступа намерено отсутствует, чтобы переменная была доступна только в пакете
    static final Config config = new LocalConfig();

    // приватный конструктор и статичное поле config это реализация синглтона:
    // мы не можем создавать экземпляр класса изза закрытого конструктора
    // без ленивой инициализации - экземпляр класса создается сразу,не в момент использования
    private LocalConfig(){

    }

    @Override
    public String dbHost() {
        return "localhost";
    }
}
