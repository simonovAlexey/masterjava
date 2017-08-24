package ru.javaops.masterjava.persist.model;

import com.bertoncelj.jdbi.entitymapper.Column;
import lombok.*;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
public class City {
    @Column("id")
    @NonNull private String id;
    @NonNull private String value;
}
