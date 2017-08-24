package ru.javaops.masterjava.persist.dao;

import com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.javaops.masterjava.persist.model.Group;
import ru.javaops.masterjava.persist.model.GroupType;

import java.util.List;

public class GroupDaoTest extends AbstractDaoTest<GroupDao>{

    public static Group t7;
    public static Group t6withUser;
    public static Group m1;
    public static List<Group> groups;
    public GroupDaoTest() {
        super(GroupDao.class);
    }

    @BeforeClass
    public static void init() {
//        t6withUser = new Group(1113,"topjava6", GroupType.FINISHED,ImmutableList.of(UserTestData.ADMIN, UserTestData.USER1));
        t7 = new Group(1111,"topjava7", GroupType.FINISHED);
        m1 = new Group(1112,"masterjava1",GroupType.CURRENT);
        groups = ImmutableList.of(t7, m1);
    }

    @Test
    public void insertBatch() throws Exception {
        dao.clean();
        dao.insertBatch(groups);
        List<Group> getList = dao.getAll();
        Assert.assertEquals(getList,groups);
    }

    @Test
    public void insertOne() throws Exception {
        dao.clean();
        dao.insertOne(t7);
        List<Group> getList = dao.getAll();
        Assert.assertEquals(getList.size(),1);
        Assert.assertEquals(getList.get(0),t7);
    }

    @Test
    public void getGroupsWithUsersTest() throws Exception {
        List<Group> getList = dao.getWithUsers();
        Assert.assertEquals(getList.size(),2);
        Assert.assertEquals(getList.get(0).getUsers().size(),3);
        System.out.println(" ");
    }

}