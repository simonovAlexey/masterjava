package ru.javaops.masterjava.persist.model;

import lombok.*;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ToString(callSuper = true)
public class Project extends BaseEntity {
    private @NonNull
    String name;
    private @NonNull
    String description;
    private List<Group> groups;

    public Project(Integer id, String name, String description) {
        super(id);
        this.name = name;
        this.description = description;
    }

    public Project(Integer id, String name, String description, List<Group> groups) {
        this(id,name,description);
        this.groups = groups;
    }
}
