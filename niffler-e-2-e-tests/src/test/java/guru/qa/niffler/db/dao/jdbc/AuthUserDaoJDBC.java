package guru.qa.niffler.db.dao.jdbc;

import guru.qa.niffler.db.DataSourceDB;
import guru.qa.niffler.db.DataSourceProvider;
import guru.qa.niffler.db.dao.AuthUserDao;
import guru.qa.niffler.db.model.jpa.Authority;
import guru.qa.niffler.db.model.jpa.AuthorityEntity;
import guru.qa.niffler.db.model.jpa.UserEntity;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AuthUserDaoJDBC implements AuthUserDao {

    private static DataSource authDataSource = DataSourceProvider.INSTANCE.getDataSource(DataSourceDB.AUTH);

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
    public void deleteUser(UserEntity user) {
        try (Connection authConn = authDataSource.getConnection();
             PreparedStatement deleteAuthorityStatement = authConn.prepareStatement(
                     "DELETE FROM \"authority\"  WHERE \"user_id\" = ?"
             );
             PreparedStatement deleteUserStatement = authConn.prepareStatement(
                     "DELETE FROM \"user\"  WHERE \"id\" = ?"
             )) {
            deleteUserStatement.setObject(1, user.getId());
            deleteAuthorityStatement.setObject(1, user.getId());
            deleteAuthorityStatement.executeUpdate();
            deleteUserStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserEntity getUserById(UUID userId) {
        try (
                Connection authConn = authDataSource.getConnection();
                PreparedStatement getUserStatement = authConn.prepareStatement(
                        "SELECT username,password,enabled,account_non_expired,account_non_locked,credentials_non_expired," +
                                "\"authority\".id AS authority_id,authority FROM \"user\" " +
                                "JOIN \"authority\" ON \"user\".id=user_id " +
                                "WHERE \"user\".id = ?")) {
            getUserStatement.setObject(1, userId);
            ResultSet resultSet = getUserStatement.executeQuery();
            UserEntity userEntity = new UserEntity();
            List<AuthorityEntity> authorities = new ArrayList<>();

            while (resultSet.next()) {
                if (userEntity.getUsername() == null) {
                    userEntity.setId(userId);
                    userEntity.setUsername(resultSet.getString("username"));
                    userEntity.setPassword(resultSet.getString("password"));
                    userEntity.setEnabled(resultSet.getBoolean("enabled"));
                    userEntity.setAccountNonExpired(resultSet.getBoolean("account_non_expired"));
                    userEntity.setAccountNonLocked(resultSet.getBoolean("account_non_locked"));
                    userEntity.setCredentialsNonExpired(resultSet.getBoolean("credentials_non_expired"));
                }
                authorities.add(
                        AuthorityEntity.builder()
                                .id(UUID.fromString(resultSet.getString("authority_id")))
                                .authority(Authority.valueOf(resultSet.getString("authority"))).build());
            }
            userEntity.setAuthorities(authorities);
            return userEntity;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserEntity getUserByUsername(String username) {
        try (
                Connection authConn = authDataSource.getConnection();
                PreparedStatement getUserStatement = authConn.prepareStatement(
                        "SELECT \"user\".id,username,password,enabled,account_non_expired,account_non_locked,credentials_non_expired," +
                                "\"authority\".id AS authority_id,authority FROM \"user\" " +
                                "JOIN \"authority\" ON \"user\".id=user_id " +
                                "WHERE \"user\".username = ?")) {
            getUserStatement.setString(1, username);
            ResultSet resultSet = getUserStatement.executeQuery();
            UserEntity userEntity = new UserEntity();
            List<AuthorityEntity> authorities = new ArrayList<>();

            while (resultSet.next()) {
                if (userEntity.getUsername() == null) {
                    userEntity.setId(UUID.fromString(resultSet.getString("id")));
                    userEntity.setUsername(resultSet.getString("username"));
                    userEntity.setPassword(resultSet.getString("password"));
                    userEntity.setEnabled(resultSet.getBoolean("enabled"));
                    userEntity.setAccountNonExpired(resultSet.getBoolean("account_non_expired"));
                    userEntity.setAccountNonLocked(resultSet.getBoolean("account_non_locked"));
                    userEntity.setCredentialsNonExpired(resultSet.getBoolean("credentials_non_expired"));
                }
                authorities.add(
                        AuthorityEntity.builder()
                                .id(UUID.fromString(resultSet.getString("authority_id")))
                                .authority(Authority.valueOf(resultSet.getString("authority"))).build());
            }
            userEntity.setAuthorities(authorities);
            return userEntity;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
