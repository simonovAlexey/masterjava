package ru.javaops.masterjava.persist.model;

import lombok.*;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ToString(callSuper = true)
public class Group extends BaseEntity {
    private @NonNull String name;
    private @NonNull GroupType type;
    private  List<User> users;

    public Group(Integer id, String name, GroupType type, List<User> users) {
        this.id=id;
        this.users=users;
        this.name=name;
        this.type=type;
    }

    public Group(Integer id, String name, GroupType type) {
        super(id);
        this.name = name;
        this.type = type;
    }
}

