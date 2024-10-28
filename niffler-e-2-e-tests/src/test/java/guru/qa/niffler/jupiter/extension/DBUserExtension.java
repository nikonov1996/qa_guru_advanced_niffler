package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.db.dao.AuthUserDao;
import guru.qa.niffler.db.dao.UserDataDao;
import guru.qa.niffler.db.model.Authority;
import guru.qa.niffler.db.model.AuthorityEntity;
import guru.qa.niffler.db.model.UserDataEntity;
import guru.qa.niffler.db.model.UserEntity;
import guru.qa.niffler.jupiter.annotation.DBUser;
import guru.qa.niffler.jupiter.annotation.Entity;
import guru.qa.niffler.model.CurrencyValues;
import org.junit.jupiter.api.extension.*;

import java.util.Arrays;

public class DBUserExtension implements ParameterResolver, BeforeEachCallback, AfterEachCallback {

    public static ExtensionContext.Namespace DB_USERS_NAMESPACE = ExtensionContext.Namespace.create(DBUserExtension.class);
    private static AuthUserDao authUserDao = AuthUserDao.getInstance();
    private static UserDataDao userDataDao = UserDataDao.getInstance();

    private static UserEntity userEntity;
    private static UserDataEntity userDataEntity;

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        if (context.getRequiredTestMethod().isAnnotationPresent(DBUser.class)) {
            DBUser dbUser = context.getRequiredTestMethod().getAnnotation(DBUser.class);
            userEntity = UserEntity.builder()
                    .username(dbUser.username())
                    .password(dbUser.password())
                    .enabled(dbUser.enabled())
                    .accountNonLocked(dbUser.accountNonLocked())
                    .credentialsNonExpired(dbUser.credentialsNonExpired())
                    .accountNonExpired(dbUser.accountNonExpired())
                    .authorities(
                            Arrays.stream(Authority.values())
                                    .map(authority -> AuthorityEntity.builder()
                                            .authority(authority)
                                            .build())
                                    .toList())
                    .build();
            userDataEntity = UserDataEntity.builder()
                    .username(dbUser.username())
                    .currency(CurrencyValues.RUB)
                    .build();
            authUserDao.createUser(userEntity);
            userDataDao.createUserData(userDataEntity);
            context.getStore(DB_USERS_NAMESPACE).put(context.getUniqueId(),userEntity);
        }
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        userDataDao.deleteUserDataById(userDataEntity.getId());
        authUserDao.deleteUserById(userEntity.getId());
        context.getStore(DB_USERS_NAMESPACE).remove(context.getUniqueId());
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(UserEntity.class) &&
                parameterContext.getParameter().isAnnotationPresent(Entity.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(DB_USERS_NAMESPACE).get(extensionContext.getUniqueId(), UserEntity.class);
    }
}
