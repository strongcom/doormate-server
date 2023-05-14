package com.strongcom.doormate.domain;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "authority")
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