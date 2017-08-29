package ru.javaops.masterjava.service.dao;

import com.bertoncelj.jdbi.entitymapper.EntityMapperFactory;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import ru.javaops.masterjava.persist.dao.AbstractDao;
import ru.javaops.masterjava.service.model.EmailResult;

import java.util.List;

@RegisterMapperFactory(EntityMapperFactory.class)
public abstract class EmailResultDao implements AbstractDao {

    @Override
    @SqlUpdate("TRUNCATE email_result ")
    abstract public void clean();

    @SqlBatch("INSERT INTO email_result (email, messageId, date) VALUES (:email, :messageId, :date)")
    @GetGeneratedKeys
    public abstract void insertBatch(@BindBean List<EmailResult> emailResults);

}
