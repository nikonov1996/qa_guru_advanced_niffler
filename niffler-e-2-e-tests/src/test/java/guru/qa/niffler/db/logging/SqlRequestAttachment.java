package guru.qa.niffler.db.logging;

import io.qameta.allure.attachment.AttachmentData;

public class SqlRequestAttachment implements AttachmentData {

    private final String name;
    private final String sql;
    private final String statement;

    public SqlRequestAttachment(String name, String sql, String statement){
        this.name = name;
        this.sql = sql;
        this.statement = statement;
    }

    public String getStatement() {
        return statement;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getSql() {
        return sql;
    }
}
