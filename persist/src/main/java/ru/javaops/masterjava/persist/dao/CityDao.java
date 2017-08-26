package ru.javaops.masterjava.persist.dao;

import com.bertoncelj.jdbi.entitymapper.EntityMapperFactory;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import ru.javaops.masterjava.persist.model.City;

import java.util.List;

@RegisterMapperFactory(EntityMapperFactory.class)
public abstract class CityDao implements AbstractDao {

    @SqlUpdate("TRUNCATE cities CASCADE")
    @Override
    public abstract void clean();

    @SqlBatch("INSERT INTO cities (id, value) VALUES (:id, :value) ON CONFLICT DO NOTHING")
    public abstract int[] insertBatch(@BindBean List<City> cities);

    @SqlQuery("SELECT * FROM cities ORDER BY id")
    public abstract List<City> getAll();

    @SqlUpdate("INSERT INTO cities (id, value) VALUES (:id, :value) ")
    public abstract int insertOne(@BindBean City city);

}
