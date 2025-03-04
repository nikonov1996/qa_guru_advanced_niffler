package guru.qa.niffler.db.dao.spring;

import guru.qa.niffler.db.DataSourceDB;
import guru.qa.niffler.db.DataSourceProvider;
import guru.qa.niffler.db.dao.UserDataDao;
import guru.qa.niffler.db.model.UserDataEntity;
import guru.qa.niffler.model.CurrencyValues;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Objects;
import java.util.UUID;

public class UserDataDaoSpring implements UserDataDao {

    private final TransactionTemplate userDataTtmpl;
    private final JdbcTemplate userDataJdbcTemplate;

    public UserDataDaoSpring() {
        JdbcTransactionManager tm =
                new JdbcTransactionManager(DataSourceProvider.INSTANCE.getDataSource(DataSourceDB.USERDATA));
        this.userDataTtmpl = new TransactionTemplate(tm);
        this.userDataJdbcTemplate = new JdbcTemplate(Objects.requireNonNull(tm.getDataSource()));
    }

    @Override
    public void createUserData(UserDataEntity user) {
        userDataTtmpl.execute(status -> {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            userDataJdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO \"user\" (username, currency) " +
                                "VALUES(?, ?)", Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, user.getUsername());
                ps.setString(2, CurrencyValues.RUB.name());
                return ps;
            }, keyHolder);
            UUID generatedUserId = (UUID) keyHolder.getKeyList().getFirst().get("id");
            user.setId(generatedUserId);
            return 0;
        });

    }

    @Override
    public void deleteUserDataById(UUID userId) {
        userDataJdbcTemplate.update("DELETE FROM \"user\"  WHERE \"id\" = ?", userId);
    }
}
