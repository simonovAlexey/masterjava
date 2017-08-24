package ru.javaops.masterjava.persist.model;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GroupMapper implements ResultSetMapper<List<Group>> {

    public List<Group> map(int index, ResultSet rs, StatementContext ctx) throws SQLException {
        ArrayList<Group> result = new ArrayList<>();
        int currG = rs.getInt("idg");
        int tempG = 0;
        do {
            Group group = new Group(currG,
                    rs.getString("name"),
                    GroupType.valueOf(rs.getString("type")),
                    new ArrayList<>());
            do {
                User user = new User(rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        UserFlag.valueOf(rs.getString("flag")));
                if (user.getId() > 0) {
                    group.getUsers().add(user);
                }
                if (!rs.next()) {
                    result.add(group);
                    return result;
                }
            } while ((tempG = rs.getInt("idg")) == currG);
            currG = tempG;
            result.add(group);
        } while (true);

    }
}