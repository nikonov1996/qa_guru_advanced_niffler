package guru.qa.niffler.db.dao.spring;

import guru.qa.niffler.db.DataSourceDB;
import guru.qa.niffler.db.DataSourceProvider;
import guru.qa.niffler.db.dao.AuthUserDao;
import guru.qa.niffler.db.mapper.UserEntityRowMapper;
import guru.qa.niffler.db.model.jpa.Authority;
import guru.qa.niffler.db.model.jpa.UserEntity;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.UUID;

public class AuthUserDaoSpring implements AuthUserDao {

    private final TransactionTemplate authTtmpl;
    private final JdbcTemplate authUserJdbcTemplate;

    public AuthUserDaoSpring() {
        JdbcTransactionManager authTm =
                new JdbcTransactionManager(DataSourceProvider.INSTANCE.getDataSource(DataSourceDB.AUTH));
        this.authTtmpl = new TransactionTemplate(authTm);
        this.authUserJdbcTemplate =
                new JdbcTemplate(Objects.requireNonNull(authTm.getDataSource()));
    }

    @Override
    public void createUser(UserEntity user) {
         authTtmpl.execute(status -> {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            authUserJdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO \"user\" (username, password, enabled, account_non_expired, " +
                                "account_non_locked, credentials_non_expired)" +
                                "VALUES(?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, user.getUsername());
                ps.setString(2, pe.encode(user.getPassword()));
                ps.setBoolean(3, user.getEnabled());
                ps.setBoolean(4, user.getAccountNonExpired());
                ps.setBoolean(5, user.getAccountNonLocked());
                ps.setBoolean(6, user.getCredentialsNonExpired());
                return ps;
            }, keyHolder);
            UUID userId = (UUID) keyHolder.getKeyList().getFirst().get("id");
            authUserJdbcTemplate.batchUpdate(
                    "INSERT INTO \"authority\" (user_id, authority) VALUES(?, ?)",
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            ps.setObject(1, userId);
                            ps.setObject(2, Authority.values()[i].name());
                        }

                        @Override
                        public int getBatchSize() {
                            return Authority.values().length;
                        }
                    }
            );
             return 0;
         });
    }

    @Override
    public void deleteUser(UserEntity user) {
        authUserJdbcTemplate.update("DELETE FROM \"authority\"  WHERE \"user_id\" = ?", user.getId());
        authUserJdbcTemplate.update("DELETE FROM \"user\"  WHERE \"id\" = ?", user.getId());
    }

    @Override
    public UserEntity getUserById(UUID userId) {
        return authUserJdbcTemplate.queryForObject(
                "SELECT username,password,enabled,account_non_expired,account_non_locked,credentials_non_expired," +
                        "\"authority\".id AS authority_id,authority FROM \"user\" " +
                        "JOIN \"authority\" ON \"user\".id=user_id " +
                        "WHERE \"user\".id = ?",
                new UserEntityRowMapper(),
                userId
        );
    }

    @Override
    public UserEntity getUserByUsername(String username) {
        return authUserJdbcTemplate.queryForObject(
                "SELECT username,password,enabled,account_non_expired,account_non_locked,credentials_non_expired," +
                        "\"authority\".id AS authority_id,authority FROM \"user\" " +
                        "JOIN \"authority\" ON \"user\".id=user_id " +
                        "WHERE \"user\".username = ?",
                new UserEntityRowMapper(),
                username
        );
    }
}
