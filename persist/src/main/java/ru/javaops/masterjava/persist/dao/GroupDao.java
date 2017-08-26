package ru.javaops.masterjava.persist.dao;

import com.bertoncelj.jdbi.entitymapper.EntityMapperFactory;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import ru.javaops.masterjava.persist.model.BaseEntity;
import ru.javaops.masterjava.persist.model.Group;
import ru.javaops.masterjava.persist.model.GroupMapper;
import ru.javaops.masterjava.persist.model.User;

import java.util.List;
import java.util.stream.Collectors;

@RegisterMapperFactory(EntityMapperFactory.class)
public abstract class GroupDao implements AbstractDao {

    @CreateSqlObject
    abstract UserDao userDao();

    @CreateSqlObject
    abstract GroupRefsDao groupRefsDao();

    public List<Group> getWithUsers() {
        return getWithUsers0().get(0);
    }

    @Transaction
    public Group insert(Group group) {
        group = insertWithoutUsers(group);

        List<User> users = group.getUsers();
        if (users != null && !users.isEmpty()) {
            List<Integer> userIds = users.stream().
                    map(u -> userDao().insert(u)).
                    map(BaseEntity::getId).
                    collect(Collectors.toList());
            groupRefsDao().insertBatch(userIds, group.getId());
        }
        return group;
    }

    public Group insertWithoutUsers(Group group) {
        if (group.isNew()) {
            int id = insertGeneratedId(group);
            group.setId(id);
        } else {
            insertOne(group);
        }
        return group;
    }

    @SqlUpdate("TRUNCATE groups CASCADE")
    @Override
    public abstract void clean();

    @SqlBatch("INSERT INTO groups (id, name, type) VALUES (:id, :name, CAST(:type AS group_type)) ON CONFLICT DO NOTHING")
    public abstract int[] insertBatch(@BindBean List<Group> groups);

    @SqlQuery("SELECT * FROM groups ORDER BY id")
    public abstract List<Group> getAll();

    @SqlUpdate("INSERT INTO groups (name, type) VALUES (:name, CAST(:type AS group_type)) ON CONFLICT DO NOTHING ")
    @GetGeneratedKeys
    abstract int insertGeneratedId(@BindBean Group group);

    @SqlUpdate("INSERT INTO groups (id, name, type) VALUES (:id, :name, CAST(:type AS group_type)) ON CONFLICT DO NOTHING")
    abstract int insertOne(@BindBean Group group);

    @Mapper(GroupMapper.class)
//    @SqlQuery("SELECT g.*, u.* FROM groups as g, users as u, grouprefs as gr WHERE g.id=gr.group_id AND u.id=gr.user_id")
    @SqlQuery("SELECT g.id AS idg,g.name,g.type,u.* FROM groups as g " +
            "INNER JOIN grouprefs as gs ON (g.id=gs.group_id) " +
            "INNER JOIN users as u ON (gs.user_id = u.id)")
    public abstract List<List<Group>> getWithUsers0();

}
