package com.umsa.storage.service.mapper;

import com.umsa.storage.domain.*;
import com.umsa.storage.service.dto.FileTypeDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link FileType} and its DTO {@link FileTypeDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface FileTypeMapper extends EntityMapper<FileTypeDTO, FileType> {



    default FileType fromId(Long id) {
        if (id == null) {
            return null;
        }
        FileType fileType = new FileType();
        fileType.setId(id);
        return fileType;
    }
}
