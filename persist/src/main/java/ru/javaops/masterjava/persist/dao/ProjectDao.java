package ru.javaops.masterjava.persist.dao;

import com.bertoncelj.jdbi.entitymapper.EntityMapperFactory;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import ru.javaops.masterjava.persist.model.BaseEntity;
import ru.javaops.masterjava.persist.model.Group;
import ru.javaops.masterjava.persist.model.Project;
import ru.javaops.masterjava.persist.model.ProjectMapper;

import java.util.List;
import java.util.stream.Collectors;

@RegisterMapperFactory(EntityMapperFactory.class)
public abstract class ProjectDao implements AbstractDao {

    @CreateSqlObject
    abstract GroupDao groupDao();

    @CreateSqlObject
    abstract ProjectGroupsDao projectGroupsDao();

    @SqlUpdate("TRUNCATE projects CASCADE")
    @Override
    public abstract void clean();

    @Transaction
    public Project insert(Project pr) {
        if (pr.isNew()) {
            int id = insertGeneratedId(pr);
            pr.setId(id);
        } else {
            insertWitId(pr);
        }
        List<Group> groups = pr.getGroups();
        if (groups != null && !groups.isEmpty()) {
            List<Integer> groupIds = groups.stream().
                    map(g -> groupDao().insertWithoutUsers(g)).
                    map(BaseEntity::getId).
                    collect(Collectors.toList());
            projectGroupsDao().insertBatch(groupIds, pr.getId());
        }
        return pr;
    }

    public List<Project> getWithGroups() {
        return getWithGroups0().get(0);
    }

    @SqlUpdate("INSERT INTO projects (id, name, description) VALUES (:id, :name, :description) ")
    protected abstract void insertWitId(@BindBean Project pr);

    @SqlUpdate("INSERT INTO projects (name, description) VALUES (:name, :description) ")
    @GetGeneratedKeys
    protected abstract int insertGeneratedId(@BindBean Project pr);

    @Mapper(ProjectMapper.class)
    @SqlQuery("SELECT p.id AS idg,p.name as p_name,p.description,g.* FROM groups as g, projects as p, project_groups as gr " +
            "WHERE p.id=gr.project_id AND g.id=gr.group_id")
//    @SqlQuery("SELECT g.id AS idg,g.name,g.type,u.* FROM groups as g " +
//            "INNER JOIN grouprefs as gs ON (g.id=gs.group_id) " +
//            "INNER JOIN users as u ON (gs.user_id = u.id)")
    public abstract List<List<Project>> getWithGroups0();
}
