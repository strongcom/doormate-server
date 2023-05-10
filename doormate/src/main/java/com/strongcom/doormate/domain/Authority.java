package com.strongcom.doormate.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "AUTHORITY")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Authority {

    @Id
    @Column(name = "AUTHORITY_NAME", length = 50)
    private String authorityName;

}