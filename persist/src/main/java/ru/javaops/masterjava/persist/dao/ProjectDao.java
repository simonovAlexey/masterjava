package ru.javaops.masterjava.persist.dao;

import com.bertoncelj.jdbi.entitymapper.EntityMapperFactory;
import one.util.streamex.StreamEx;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import ru.javaops.masterjava.persist.model.Project;

import java.util.List;
import java.util.Map;

@RegisterMapperFactory(EntityMapperFactory.class)
public abstract class ProjectDao implements AbstractDao {

    @SqlUpdate("TRUNCATE project CASCADE ")
    @Override
    public abstract void clean();

    @SqlQuery("SELECT * FROM project ORDER BY name")
    public abstract List<Project> getAll();

    @SqlQuery("SELECT id FROM project where name=:name")
    public abstract int getIdByGroupName(@Bind("name") String name);

    public Map<String, Project> getAsMap() {
        return StreamEx.of(getAll()).toMap(Project::getName, g -> g);
    }

    @SqlUpdate("INSERT INTO project (name, description)  VALUES (:name, :description) ON CONFLICT DO NOTHING")
    @GetGeneratedKeys
    public abstract int insertGeneratedId(@BindBean Project project);

    public void insert(Project project) {
        int id = insertGeneratedId(project);
        if (id==0) id = getIdByGroupName(project.getName());
        project.setId(id);
    }
}
