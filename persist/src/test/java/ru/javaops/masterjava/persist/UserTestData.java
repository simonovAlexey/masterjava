package ru.javaops.masterjava.persist;

import com.google.common.collect.ImmutableList;
import ru.javaops.masterjava.persist.dao.CityDao;
import ru.javaops.masterjava.persist.dao.UserDao;
import ru.javaops.masterjava.persist.model.City;
import ru.javaops.masterjava.persist.model.User;
import ru.javaops.masterjava.persist.model.UserFlag;

import java.util.Arrays;
import java.util.List;

/**
 * gkislin
 * 14.11.2016
 */
public class UserTestData {
    public static User ADMIN;
    public static User DELETED;
    public static User FULL_NAME;
    public static User USER1;
    public static User USER2;
    public static User USER3;
    public static List<User> FIST5_USERS;

    public static void init() {
        ADMIN = new User("Admin", "admin@javaops.ru", "spb", UserFlag.superuser);
        DELETED = new User("Deleted", "deleted@yandex.ru", "spb", UserFlag.deleted);
        FULL_NAME = new User("Full Name", "gmail@gmail.com", "spb", UserFlag.active);
        USER1 = new User("User1", "user1@gmail.com", "msk", UserFlag.active);
        USER2 = new User("User2", "user2@yandex.ru", "msk", UserFlag.active);
        USER3 = new User("User3", "user3@yandex.ru", "msk", UserFlag.active);
        FIST5_USERS = ImmutableList.of(ADMIN, DELETED, FULL_NAME, USER1, USER2);
    }

    public static void setUp() {
        UserDao dao = DBIProvider.getDao(UserDao.class);
        CityDao cityDao = DBIProvider.getDao(CityDao.class);
        cityDao.clean();
        cityDao.insertBatch(Arrays.asList(new City("msk", "MSK"), new City("spb", "SPB")));
        dao.clean();
        DBIProvider.getDBI().useTransaction((conn, status) -> {
            FIST5_USERS.forEach(dao::insert);
            dao.insert(USER3);
        });
    }
}
