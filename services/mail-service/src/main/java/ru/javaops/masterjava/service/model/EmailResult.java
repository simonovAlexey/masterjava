package ru.javaops.masterjava.service.model;

import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
public class EmailResult {

    private Integer id;
    @NonNull
    private String email;
    @NonNull
    private String messageId;
    @NonNull
    private Date date;
}
