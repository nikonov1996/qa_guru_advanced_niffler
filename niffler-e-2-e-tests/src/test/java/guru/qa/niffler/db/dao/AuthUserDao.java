package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.dao.jdbc.AuthUserDaoJDBC;
import guru.qa.niffler.db.dao.jpa.AuthUserDaoHibernate;
import guru.qa.niffler.db.dao.spring.AuthUserDaoSpring;
import guru.qa.niffler.db.model.jpa.UserEntity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

public interface AuthUserDao {

 /*
     Смысл паттерна DAO в том, чтобы отвязать модель данных которая используется в проекте
     от конкретной реализации хранения данных (базы данных, файлов, RAM). Т.е. к примеру модель пользователя
     для авторизации должна быть универсальной, и не зависить от бд в которую будут отправляться данные для хранения
     авторизованного пользователя.
     В тесте мы используем такую запись AuthUserDao authUser = AuthUserDao.getInstance();
     Следовательно, сам тест ничего не знает о том, с какой базой данных он работает. Это называется слабая связностью
  */
    static AuthUserDao getInstance() {
        String dbImpl = "hibernate";//System.getProperty("db.impl");
        return switch (dbImpl) {
            case "spring" -> new AuthUserDaoSpring();
            case "hibernate" -> new AuthUserDaoHibernate();
            default -> new AuthUserDaoJDBC();
        };
    }

    PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder(); // для шифрования пароля, чтобы корректно положить его в бд

    void createUser(UserEntity user); // модели которые мапятся на таблицу баззы данных имеют в названии слово Entity

    void deleteUser(guru.qa.niffler.db.model.jpa.UserEntity user);


    UserEntity getUserById(UUID userId);
    UserEntity getUserByUsername(String username);

}
