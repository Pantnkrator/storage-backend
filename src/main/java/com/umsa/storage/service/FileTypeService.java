package com.umsa.storage.service;

import com.umsa.storage.domain.FileType;
import com.umsa.storage.repository.FileTypeRepository;
import com.umsa.storage.service.dto.FileTypeDTO;
import com.umsa.storage.service.mapper.FileTypeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

/**
 * Service Implementation for managing {@link FileType}.
 */
@Service
@Transactional
public class FileTypeService {

    private final Logger log = LoggerFactory.getLogger(FileTypeService.class);

    private final FileTypeRepository fileTypeRepository;

    private final FileTypeMapper fileTypeMapper;

    public FileTypeService(FileTypeRepository fileTypeRepository, FileTypeMapper fileTypeMapper) {
        this.fileTypeRepository = fileTypeRepository;
        this.fileTypeMapper = fileTypeMapper;
    }

    /**
     * Save a fileType.
     *
     * @param fileTypeDTO the entity to save.
     * @return the persisted entity.
     */
    public FileTypeDTO save(FileTypeDTO fileTypeDTO) {
        log.debug("Request to save FileType : {}", fileTypeDTO);
        FileType fileType = fileTypeMapper.toEntity(fileTypeDTO);
        fileType = fileTypeRepository.save(fileType);
        return fileTypeMapper.toDto(fileType);
    }

    /**
     * Get all the fileTypes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<FileTypeDTO> findAll(Pageable pageable) {
        log.debug("Request to get all FileTypes");
        return fileTypeRepository.findAll(pageable)
            .map(fileTypeMapper::toDto);
    }


    /**
     * Get one fileType by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<FileTypeDTO> findOne(Long id) {
        log.debug("Request to get FileType : {}", id);
        return fileTypeRepository.findById(id)
            .map(fileTypeMapper::toDto);
    }

    /**
     * Delete the fileType by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete FileType : {}", id);
        fileTypeRepository.deleteById(id);
    }
    public void audit(FileType pago) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        if (pago.getCreatedBy() == null) {
            pago.setCreatedBy(currentUser);
            pago.setCreatedDate(Instant.now());
        } else {
            pago.setLastModifiedBy(currentUser);
            pago.setLastModifiedDate(Instant.now());
        }
    }
}
