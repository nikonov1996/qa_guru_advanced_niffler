package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.DataSourceDB;
import guru.qa.niffler.db.DataSourceProvider;
import guru.qa.niffler.db.model.Authority;
import guru.qa.niffler.db.model.UserDataEntity;
import guru.qa.niffler.db.model.UserEntity;
import guru.qa.niffler.model.CurrencyValues;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AuthUserDaoJDBC implements AuthUserDao, UserDataDao {

    private static DataSource authDataSource = DataSourceProvider.INSTANCE.getDataSource(DataSourceDB.AUTH);
    private static DataSource userDataSource = DataSourceProvider.INSTANCE.getDataSource(DataSourceDB.USERDATA);

    @Override
    public void createUser(UserEntity user) {
        try (
                Connection userAuthConn = authDataSource.getConnection()
        ) {
            userAuthConn.setAutoCommit(false);
            try (PreparedStatement usersPrepared = userAuthConn.prepareStatement(
                    "INSERT INTO \"user\" (username, password, enabled, account_non_expired, " +
                            "account_non_locked, credentials_non_expired)" +
                            "VALUES(?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
                 PreparedStatement authorityPrepared = userAuthConn.prepareStatement(
                         "INSERT INTO \"authority\" (user_id, authority) VALUES(?, ?)");
            ) {
                usersPrepared.setString(1, user.getUsername());
                usersPrepared.setString(2, pe.encode(user.getPassword()));
                usersPrepared.setBoolean(3, user.getEnabled());
                usersPrepared.setBoolean(4, user.getAccountNonExpired());
                usersPrepared.setBoolean(5, user.getAccountNonLocked());
                usersPrepared.setBoolean(6, user.getCredentialsNonExpired());

                usersPrepared.executeUpdate();

                UUID generatedUserId = null;
                try (ResultSet generatedKeys = usersPrepared.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        generatedUserId = UUID.fromString(generatedKeys.getString("id"));
                    }
                } catch (SQLException e) {
                    throw new IllegalStateException("Can't take id from ResultSet");
                }

                for (Authority authority : Authority.values()) {
                    authorityPrepared.setObject(1, generatedUserId);
                    authorityPrepared.setString(2, authority.name());
                    authorityPrepared.addBatch(); // метод собирает в себя список сформированных sql запросов
                    authorityPrepared.clearParameters(); // необходимо очистить параметры, иначе в следующей итерации цикла подставятся предыдущие
                }
                authorityPrepared.executeBatch();
                user.setId(generatedUserId);
                userAuthConn.commit();
                userAuthConn.setAutoCommit(true);

            } catch (SQLException e) {
                userAuthConn.rollback();
                userAuthConn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createUserData(UserDataEntity user) {
        try(Connection userDataConn = userDataSource.getConnection()){
            try (PreparedStatement userDataPrepared = userDataConn.prepareStatement(
                    "INSERT INTO \"user\" (username, currency) " +
                            "VALUES(?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
            ) {
                userDataPrepared.setString(1, user.getUsername());
                userDataPrepared.setString(2, CurrencyValues.RUB.name());
                userDataPrepared.executeUpdate();
                UUID generatedUserId = null;
                try (ResultSet generatedKeys = userDataPrepared.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        generatedUserId = UUID.fromString(generatedKeys.getString("id"));
                    }
                    user.setId(generatedUserId);
                } catch (SQLException e) {
                    throw new IllegalStateException("Can't take id from ResultSet");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteUserDataById(UUID userId) {

    }

    @Override
    public void deleteUserById(UUID userId) {

    }
}
