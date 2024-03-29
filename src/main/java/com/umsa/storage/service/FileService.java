package com.umsa.storage.service;

import com.umsa.storage.domain.File;
import com.umsa.storage.repository.FileRepository;
import com.umsa.storage.service.dto.FileDTO;
import com.umsa.storage.service.mapper.FileMapper;
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
 * Service Implementation for managing {@link File}.
 */
@Service
@Transactional
public class FileService {

    private final Logger log = LoggerFactory.getLogger(FileService.class);

    private final FileRepository fileRepository;

    private final FileMapper fileMapper;

    public FileService(FileRepository fileRepository, FileMapper fileMapper) {
        this.fileRepository = fileRepository;
        this.fileMapper = fileMapper;
    }

    /**
     * Save a file.
     *
     * @param fileDTO the entity to save.
     * @return the persisted entity.
     */
    public FileDTO save(FileDTO fileDTO) {
        log.debug("Request to save File : {}", fileDTO);
        File file = fileMapper.toEntity(fileDTO);
        audit(file);
        file = fileRepository.save(file);
        return fileMapper.toDto(file);
    }

    /**
     * Get all the files.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<FileDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Files");
        return fileRepository.findAll(pageable)
            .map(fileMapper::toDto);
    }


    /**
     * Get one file by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<FileDTO> findOne(Long id) {
        log.debug("Request to get File : {}", id);
        return fileRepository.findById(id)
            .map(fileMapper::toDto);
    }

    /**
     * Delete the file by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete File : {}", id);
        fileRepository.deleteById(id);
    }
    public void audit(File file) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        if (file.getCreatedBy() == null) {
            file.setCreatedBy(currentUser);
            file.setCreatedDate(Instant.now());
        } else {
            file.setLastModifiedBy(currentUser);
            file.setLastModifiedDate(Instant.now());
        }
    }
}
