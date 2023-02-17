package com.os.course.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SongMetadataDto {
    private long id;
    private String name;
    private String artist;
    private String album;

    private Integer length;

    private Long resourceId;
}
