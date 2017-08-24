package ru.javaops.masterjava.persist.dao;

import com.bertoncelj.jdbi.entitymapper.EntityMapperFactory;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import ru.javaops.masterjava.persist.model.Group;
import ru.javaops.masterjava.persist.model.GroupMapper;

import java.util.List;

@RegisterMapperFactory(EntityMapperFactory.class)
public abstract class GroupDao implements AbstractDao {

    @SqlUpdate("TRUNCATE groups CASCADE")
    @Override
    public abstract void clean();

    @SqlBatch("INSERT INTO groups (id, name, type) VALUES (:id, :name, CAST(:type AS group_type)) ON CONFLICT DO NOTHING")
    public abstract int[] insertBatch(@BindBean List<Group> groups);

    @SqlQuery("SELECT * FROM groups ORDER BY id")
    public abstract List<Group> getAll();

    @SqlUpdate("INSERT INTO groups (id, name, type) VALUES (:id, :name, CAST(:type AS group_type)) ")
    abstract int insertOne(@BindBean Group group);

    @Mapper(GroupMapper.class)
//    @SqlQuery("SELECT Account.id, Account.name, User.id as u_id, User.name as u_name FROM Account LEFT JOIN User ON User.accountId = Account.id WHERE Account.id = :id")
//    @SqlQuery("SELECT g.*, u.* FROM groups as g, users as u, grouprefs as gr WHERE g.id=gr.group_id AND u.id=gr.user_id")
    @SqlQuery("SELECT g.id AS idg,g.name,g.type,u.* FROM groups as g " +
            "INNER JOIN grouprefs as gs ON (g.id=gs.group_id) " +
            "INNER JOIN users as u ON (gs.user_id = u.id)")
    public abstract List<Group> getWithUsers();

}
