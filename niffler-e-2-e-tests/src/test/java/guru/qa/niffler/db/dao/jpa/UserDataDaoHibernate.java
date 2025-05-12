package guru.qa.niffler.db.dao.jpa;

import guru.qa.niffler.db.DataSourceDB;
import guru.qa.niffler.db.dao.UserDataDao;
import guru.qa.niffler.db.jpa.EntityManagerFactoryProvider;
import guru.qa.niffler.db.jpa.JpaService;
import guru.qa.niffler.db.model.jpa.UserDataEntity;

public class UserDataDaoHibernate extends JpaService implements UserDataDao {

    public UserDataDaoHibernate() {
        super(EntityManagerFactoryProvider.INSTANCE
                .getDataSource(DataSourceDB.USERDATA)
                .createEntityManager());
    }

    @Override
    public void createUserData(UserDataEntity user) {

    }

    @Override
    public void deleteUser(UserDataEntity user) {

    }
}
