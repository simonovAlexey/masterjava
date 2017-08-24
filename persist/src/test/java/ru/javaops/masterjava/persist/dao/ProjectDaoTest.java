package ru.javaops.masterjava.persist.dao;

import com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.javaops.masterjava.persist.model.Group;
import ru.javaops.masterjava.persist.model.GroupType;
import ru.javaops.masterjava.persist.model.Project;

import java.util.List;

public class ProjectDaoTest extends AbstractDaoTest<ProjectDao>{

    public static Group t7;
    public static Group m1;
    public static List<Group> groups;
    public static Project topjava;
    public ProjectDaoTest() {
        super(ProjectDao.class);
    }

    @BeforeClass
    public static void init() {
        t7 = new Group(1111,"topjava7", GroupType.FINISHED);
        m1 = new Group(1112,"masterjava1",GroupType.CURRENT);
        groups = ImmutableList.of(t7, m1);
        topjava = new Project(null,"TOPJAVA","описание TOPJAVA",groups);
    }

    @Test
    public void insert() throws Exception {
        Project insert = dao.insert(topjava);
        System.out.println(" ");
    }

    @Test
    public void getWithGroups() throws Exception {
        List<Project> withGroups = dao.getWithGroups();
        Assert.assertEquals(withGroups.get(0),topjava);
    }

}