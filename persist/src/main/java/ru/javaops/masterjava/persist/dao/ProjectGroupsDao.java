package ru.javaops.masterjava.persist.dao;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

import java.util.List;

public abstract class ProjectGroupsDao implements AbstractDao {

    @Override
    @SqlUpdate("TRUNCATE project_groups CASCADE")
    abstract public void clean();

    @SqlBatch("INSERT INTO project_groups (project_id, group_id) VALUES (:pId, :groupIds)" +
            "ON CONFLICT DO NOTHING")
    abstract public int[] insertBatch(@Bind(value = "groupIds") List<Integer> groupIds, @Bind(value = "pId") int projectId);
}
