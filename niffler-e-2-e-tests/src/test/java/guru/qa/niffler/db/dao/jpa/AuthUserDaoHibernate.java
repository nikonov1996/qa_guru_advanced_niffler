package guru.qa.niffler.db.dao.jpa;

import guru.qa.niffler.db.DataSourceDB;
import guru.qa.niffler.db.dao.AuthUserDao;
import guru.qa.niffler.db.jpa.EntityManagerFactoryProvider;
import guru.qa.niffler.db.jpa.JpaService;
import guru.qa.niffler.db.model.jpa.UserEntity;
import jakarta.persistence.EntityManager;

import java.util.UUID;

public class AuthUserDaoHibernate extends JpaService implements AuthUserDao {

    protected EntityManager em;
    public AuthUserDaoHibernate() {
        super(EntityManagerFactoryProvider.INSTANCE
                .getDataSource(DataSourceDB.AUTH)
                .createEntityManager());
    }

    @Override
    public void createUser(UserEntity user) {
        user.setPassword(pe.encode(user.getPassword()));
        create(user);
    }

    @Override
    public void deleteUser(UserEntity user) {
        remove(user);
    }

    @Override
    public UserEntity getUserById(UUID userId) {
        return em.createQuery("select u from UserEntity u where u.id", UserEntity.class)
                .setParameter("id",userId)
                .getSingleResult();
    }

    @Override
    public UserEntity getUserByUsername(String username) {
        return em.createQuery("select u from UserEntity u where u.username", UserEntity.class)
                .setParameter("username",username)
                .getSingleResult();
    }
}
