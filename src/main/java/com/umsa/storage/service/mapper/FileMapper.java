package com.umsa.storage.service.mapper;

import com.umsa.storage.domain.*;
import com.umsa.storage.service.dto.FileDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link File} and its DTO {@link FileDTO}.
 */
@Mapper(componentModel = "spring", uses = {FileTypeMapper.class})
public interface FileMapper extends EntityMapper<FileDTO, File> {

    @Mapping(source = "fileType.id", target = "fileTypeId")
    FileDTO toDto(File file);

    @Mapping(source = "fileTypeId", target = "fileType")
    File toEntity(FileDTO fileDTO);

    default File fromId(Long id) {
        if (id == null) {
            return null;
        }
        File file = new File();
        file.setId(id);
        return file;
    }
}
