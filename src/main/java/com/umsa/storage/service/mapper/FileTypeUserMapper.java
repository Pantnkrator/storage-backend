package com.umsa.storage.service.mapper;

import com.umsa.storage.domain.*;
import com.umsa.storage.service.dto.FileTypeUserDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link FileTypeUser} and its DTO {@link FileTypeUserDTO}.
 */
@Mapper(componentModel = "spring", uses = {FileTypeMapper.class, UserMapper.class})
public interface FileTypeUserMapper extends EntityMapper<FileTypeUserDTO, FileTypeUser> {

    @Mapping(source = "fileType.id", target = "fileTypeId")
    @Mapping(source = "user.id", target = "userId")
    FileTypeUserDTO toDto(FileTypeUser fileTypeUser);

    @Mapping(source = "fileTypeId", target = "fileType")
    @Mapping(source = "userId", target = "user")
    FileTypeUser toEntity(FileTypeUserDTO fileTypeUserDTO);

    default FileTypeUser fromId(Long id) {
        if (id == null) {
            return null;
        }
        FileTypeUser fileTypeUser = new FileTypeUser();
        fileTypeUser.setId(id);
        return fileTypeUser;
    }
}
