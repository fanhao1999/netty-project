package com.fh.netty.demo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Session {

    // 用户唯一性标识
    private String userId;

    private String username;
}
