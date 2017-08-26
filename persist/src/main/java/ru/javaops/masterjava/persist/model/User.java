package ru.javaops.masterjava.persist.model;

import com.bertoncelj.jdbi.entitymapper.Column;
import lombok.*;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class User extends BaseEntity {
    @Column("full_name")
    private @NonNull String fullName;
    private @NonNull String email;
    private @NonNull String city;
    private @NonNull UserFlag flag;

    public User(Integer id, String fullName, String email,String city, UserFlag flag) {
        this(fullName, email,city, flag);
        this.id=id;
    }
}