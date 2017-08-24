package ru.javaops.masterjava.persist.model;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProjectMapper implements ResultSetMapper<List<Project>> {
    @Override
    public List<Project> map(int index, ResultSet rs, StatementContext ctx) throws SQLException {
        ArrayList<Project> result = new ArrayList<>();
        int currP = rs.getInt("idg");
        int tempG = 0;
        do {
            Project project = new Project(currP,
                    rs.getString("p_name"),
                    rs.getString("description"),
                    new ArrayList<>());
            do {
                Group group = new Group(rs.getInt("id"),
                        rs.getString("name"),
                        GroupType.valueOf(rs.getString("type")));
                if (group.getId() > 0) {
                    project.getGroups().add(group);
                }
                if (!rs.next()) {
                    result.add(project);
                    return result;
                }
            } while ((tempG = rs.getInt("idg")) == currP);
            currP = tempG;
            result.add(project);
        } while (true);
    }
}
