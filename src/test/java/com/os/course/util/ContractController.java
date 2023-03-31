package com.os.course.util;

import com.os.course.model.dto.SongMetadataDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/test/contracts")
public class ContractController {
    @Autowired
    private MicroserviceUtil microserviceUtil;

    @Autowired
    private MicroserviceProperties microserviceProperties;

    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public SongMetadataDto save(@RequestBody SongMetadataDto songMetadataDto) {
        return microserviceUtil.postObject("http://localhost:8082" + microserviceProperties.getSongServiceUrl(), songMetadataDto, SongMetadataDto.class);
    }
}
