package com.strongcom.doormate.domain;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@RedisHash("refreshToken")
@Data
@Builder
@EqualsAndHashCode
@ToString
public class RefreshToken implements Serializable {

    private static final long serialVersionUID = -6306085255437375959L;

    @Id
    private String id;

    private String username;

    private String password;

    private Date expiryDate;
}
