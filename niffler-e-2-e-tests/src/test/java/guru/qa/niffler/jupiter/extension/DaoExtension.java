package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.db.dao.AuthUserDao;
import guru.qa.niffler.db.dao.UserDataDao;
import guru.qa.niffler.jupiter.annotation.Dao;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class DaoExtension implements TestInstancePostProcessor {
    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
        List<Field> authUserDaoFields = Arrays.stream(testInstance.getClass().getDeclaredFields())
                .filter(field ->
                        field.isAnnotationPresent(Dao.class) &&
                                (field.getType().isAssignableFrom(UserDataDao.class) ||
                                        field.getType().isAssignableFrom(AuthUserDao.class)))
                .toList();
        authUserDaoFields.forEach(field -> {
            field.setAccessible(true);
            try {
                field.set(testInstance, AuthUserDao.getInstance());
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });

    }
}
