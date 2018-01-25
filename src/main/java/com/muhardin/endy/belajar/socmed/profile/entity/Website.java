package com.muhardin.endy.belajar.socmed.profile.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity @Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"user"})})
@Data
public class Website {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @NotNull @NotEmpty
    private String user;
    private String url;
    private String profileId;
    private String accessToken;
    private String refreshToken;

    @Temporal(TemporalType.TIMESTAMP)
    private Date tokenExpireDate;
}
