package ru.javaops.masterjava.persist.dao;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

import java.util.List;

public abstract class GroupRefsDao implements AbstractDao {

    @Override
    @SqlUpdate("TRUNCATE grouprefs CASCADE")
    abstract public void clean();

    @SqlBatch("INSERT INTO grouprefs (user_id, group_id) VALUES (:userIds, :groupId)" +
            "ON CONFLICT DO NOTHING")
    abstract public int[] insertBatch(@Bind(value = "userIds") List<Integer> userIds, @Bind(value = "groupId") int groupId);
}
