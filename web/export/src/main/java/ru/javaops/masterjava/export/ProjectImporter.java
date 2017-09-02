package ru.javaops.masterjava.export;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.GroupDao;
import ru.javaops.masterjava.persist.dao.ProjectDao;
import ru.javaops.masterjava.persist.model.Group;
import ru.javaops.masterjava.persist.model.GroupType;
import ru.javaops.masterjava.persist.model.Project;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class ProjectImporter {
    private final ProjectDao projectDao = DBIProvider.getDao(ProjectDao.class);
    private final GroupDao groupDao = DBIProvider.getDao(GroupDao.class);

    public Map<String,Group> process(StaxStreamProcessor processor) throws XMLStreamException {
        String element;
        Project project = null;
        List<Group> groups = new ArrayList<>();
        while ((element = processor.doUntilAny(XMLEvent.START_ELEMENT, "Project", "Group","Cities")) != null) {
            if (element.equals("Cities")) break;
            if (element.equals("Project")) {
                val name = processor.getAttribute("name");
                val description = processor.getElementValue("description");
                project = new Project(name, description);
                projectDao.insert(project);
            } else {
                String name = processor.getAttribute("name");
                GroupType type = GroupType.valueOf(processor.getAttribute("type"));
                Integer projectId = null;
                if (project != null) projectId = project.getId();
                Group group = new Group(name, type, projectId);
                groupDao.insert(group);
                groups.add(group);
            }
        }
        return groupDao.getAsMap();
    }
}
