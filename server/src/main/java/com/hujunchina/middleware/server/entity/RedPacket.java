package com.hujunchina.middleware.server.entity;

import lombok.*;

import java.io.Serializable;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RedPacket implements Serializable {
    private Integer uid;
    @NonNull
    private Integer total;
    @NonNull
    private Integer amount;
}
