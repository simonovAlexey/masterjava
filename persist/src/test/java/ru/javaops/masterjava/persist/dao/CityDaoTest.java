package ru.javaops.masterjava.persist.dao;

import com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.javaops.masterjava.persist.model.City;

import java.util.List;

public class CityDaoTest extends AbstractDaoTest<CityDao> {
    public static List<City> cities;
    public static City mns;
    public static City msk;

    public CityDaoTest() {
        super(CityDao.class);
    }

    @BeforeClass
    public static void init() {
        msk = new City("mow", "Москва");
        mns = new City("mnsk", "Минск");
        cities = ImmutableList.of(mns, msk);
    }

    @Test
    public void insertBatchTest() throws Exception {
        dao.clean();
        dao.insertBatch(cities);
        List<City> getList = dao.getAll();
        Assert.assertEquals(getList,cities);
    }
    @Test
    public void insertTest() throws Exception {
        dao.clean();
        dao.insertOne(mns);
        List<City> getList = dao.getAll();
        Assert.assertEquals(getList.size(),1);
        Assert.assertEquals(getList.get(0),mns);
    }

}