package guru.qa.niffler.db.mapper;

import guru.qa.niffler.db.model.UserEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserEntityRowMapper implements RowMapper<UserEntity> {
    @Override
    public UserEntity mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(UUID.fromString(resultSet.getString("id")));
        userEntity.setUsername(resultSet.getString("username"));
        userEntity.setPassword(resultSet.getString("password"));
        userEntity.setEnabled(resultSet.getBoolean("enabled"));
        userEntity.setAccountNonExpired(resultSet.getBoolean("account_non_expired"));
        userEntity.setAccountNonLocked(resultSet.getBoolean("account_non_locked"));
        userEntity.setCredentialsNonExpired(resultSet.getBoolean("credentials_non_expired"));
        return userEntity;
    }
}
