package com.os.course.util;

import com.os.course.model.dto.SongMetadataDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.springframework.stereotype.Component;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
@Slf4j
public class MetadataUtil {
    public SongMetadataDto createMetadata(byte[] file, long resourceID) {
        SongMetadataDto dto = new SongMetadataDto();

        try (InputStream inputStream = new ByteArrayInputStream(file)) {
            ContentHandler handler = new DefaultHandler();
            Metadata metadata = new Metadata();
            Parser parser = new Mp3Parser();
            ParseContext parseCtx = new ParseContext();
            parser.parse(inputStream, handler, metadata, parseCtx);
            dto.setResourceId(resourceID);
            dto.setName(metadata.get(Constant.SONG_NAME));
            dto.setArtist(metadata.get(Constant.SONG_ARTIST));
            dto.setAlbum(metadata.get(Constant.SONG_ALBUM));
            dto.setLength(file.length);
        }  catch (TikaException | IOException | SAXException e) {
            log.error(e.getMessage(), e);
        }
        return dto;
    }
}
