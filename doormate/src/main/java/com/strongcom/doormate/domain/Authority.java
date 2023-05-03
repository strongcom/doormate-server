package com.strongcom.doormate.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "authority")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Authority {

    @Id
    @Column(name = "authority_name", length = 50)
    private String authorityName;
}