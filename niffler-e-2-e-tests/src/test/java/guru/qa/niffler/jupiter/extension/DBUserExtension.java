package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.db.dao.AuthUserDao;
import guru.qa.niffler.db.dao.UserDataDao;
import guru.qa.niffler.db.model.jpa.Authority;
import guru.qa.niffler.db.model.jpa.AuthorityEntity;
import guru.qa.niffler.db.model.jpa.UserDataEntity;
import guru.qa.niffler.db.model.jpa.UserEntity;
import guru.qa.niffler.grpc.CurrencyValues;
import guru.qa.niffler.jupiter.annotation.DBUser;
import guru.qa.niffler.jupiter.annotation.Entity;
import org.junit.jupiter.api.extension.*;

import java.util.Arrays;

public class DBUserExtension implements ParameterResolver, BeforeEachCallback, AfterEachCallback {

    public static ExtensionContext.Namespace DB_USERS_NAMESPACE = ExtensionContext.Namespace.create(DBUserExtension.class);
    private static AuthUserDao authUserDao = AuthUserDao.getInstance();
    private static UserDataDao userDataDao = UserDataDao.getInstance();

    private static UserEntity userEntity = new UserEntity();
    private static UserDataEntity userDataEntity = new UserDataEntity();

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        if (context.getRequiredTestMethod().isAnnotationPresent(DBUser.class)) {
            DBUser dbUser = context.getRequiredTestMethod().getAnnotation(DBUser.class);

            userEntity.setUsername(dbUser.username());
            userEntity.setPassword(dbUser.password());
            userEntity.setEnabled(dbUser.enabled());
            userEntity.setAccountNonLocked(dbUser.accountNonLocked());
            userEntity.setCredentialsNonExpired(dbUser.credentialsNonExpired());
            userEntity.setAccountNonExpired(dbUser.accountNonExpired());
            userEntity.setAuthorities(
                            Arrays.stream(Authority.values())
                                    .map(authority -> {
                                        AuthorityEntity authorityEntity = new AuthorityEntity();
                                                authorityEntity.setAuthority(authority);
                                                authorityEntity.setUser(userEntity);
                                                return authorityEntity;
                                    })
                                    .toList());
            userDataEntity.setUsername(dbUser.username());
            userDataEntity.setCurrency(CurrencyValues.RUB);

            authUserDao.createUser(userEntity);
            userDataDao.createUserData(userDataEntity);
            context.getStore(DB_USERS_NAMESPACE).put(context.getUniqueId(),userEntity);
        }
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        userDataDao.deleteUser(userDataEntity);
        authUserDao.deleteUser(userEntity);
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
