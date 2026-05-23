package com.campus.lostfound.vo;

import lombok.Data;

@Data
public class MatchVO {

    private Long id;
    private PostVO lostPost;
    private PostVO foundPost;
    private Integer score;
    private String status;
    private String reason;
}
