package guru.qa.niffler.db.dao.jdbc;

import guru.qa.niffler.db.DataSourceDB;
import guru.qa.niffler.db.DataSourceProvider;
import guru.qa.niffler.db.dao.UserDataDao;
import guru.qa.niffler.db.model.jpa.UserDataEntity;
import guru.qa.niffler.model.CurrencyValues;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserDataDaoJDBC implements UserDataDao {

    private static DataSource userDataSource = DataSourceProvider.INSTANCE.getDataSource(DataSourceDB.USERDATA);

    @Override
    public void createUserData(UserDataEntity user) {
        try (Connection userDataConn = userDataSource.getConnection()) {
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
    public void deleteUser(UserDataEntity user) {
        try (Connection userDataConn = userDataSource.getConnection();
             PreparedStatement deleteStatement = userDataConn.prepareStatement(
                     "DELETE FROM \"user\"  WHERE \"id\" = ?"
             )) {
            deleteStatement.setObject(1, user.getId());
            deleteStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserDataEntity getUserDataByUsername(UserDataEntity user) {
        UserDataEntity userData = new UserDataEntity();
        try(Connection userDataConn = userDataSource.getConnection();
        PreparedStatement getUserDataStatement = userDataConn.prepareStatement(
                "SELECT * FROM \"user\" WHERE \"username\" = ?")) {
            getUserDataStatement.setObject(1, user.getUsername());
            getUserDataStatement.execute();
            ResultSet resultSet = getUserDataStatement.getResultSet();
            if (resultSet.next()){
                userData.setId(resultSet.getObject("id", UUID.class));
                userData.setUsername(resultSet.getString("username"));
                userData.setCurrency(CurrencyValues.valueOf(resultSet.getString("currency")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return userData;
    }
}
