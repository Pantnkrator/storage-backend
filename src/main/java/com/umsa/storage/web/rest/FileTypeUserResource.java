package com.umsa.storage.web.rest;

import com.umsa.storage.service.FileTypeUserService;
import com.umsa.storage.web.rest.errors.BadRequestAlertException;
import com.umsa.storage.service.dto.FileTypeUserDTO;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.umsa.storage.domain.FileTypeUser}.
 */
@RestController
@RequestMapping("/api")
public class FileTypeUserResource {

    private final Logger log = LoggerFactory.getLogger(FileTypeUserResource.class);

    private static final String ENTITY_NAME = "fileTypeUser";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FileTypeUserService fileTypeUserService;

    public FileTypeUserResource(FileTypeUserService fileTypeUserService) {
        this.fileTypeUserService = fileTypeUserService;
    }

    /**
     * {@code POST  /file-type-users} : Create a new fileTypeUser.
     *
     * @param fileTypeUserDTO the fileTypeUserDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new fileTypeUserDTO, or with status {@code 400 (Bad Request)} if the fileTypeUser has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/file-type-users")
    public ResponseEntity<FileTypeUserDTO> createFileTypeUser(@RequestBody FileTypeUserDTO fileTypeUserDTO) throws URISyntaxException {
        log.debug("REST request to save FileTypeUser : {}", fileTypeUserDTO);
        if (fileTypeUserDTO.getId() != null) {
            throw new BadRequestAlertException("A new fileTypeUser cannot already have an ID", ENTITY_NAME, "idexists");
        }
        FileTypeUserDTO result = fileTypeUserService.save(fileTypeUserDTO);
        return ResponseEntity.created(new URI("/api/file-type-users/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /file-type-users} : Updates an existing fileTypeUser.
     *
     * @param fileTypeUserDTO the fileTypeUserDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated fileTypeUserDTO,
     * or with status {@code 400 (Bad Request)} if the fileTypeUserDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the fileTypeUserDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/file-type-users")
    public ResponseEntity<FileTypeUserDTO> updateFileTypeUser(@RequestBody FileTypeUserDTO fileTypeUserDTO) throws URISyntaxException {
        log.debug("REST request to update FileTypeUser : {}", fileTypeUserDTO);
        if (fileTypeUserDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        FileTypeUserDTO result = fileTypeUserService.save(fileTypeUserDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, fileTypeUserDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /file-type-users} : get all the fileTypeUsers.
     *

     * @param pageable the pagination information.

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of fileTypeUsers in body.
     */
    @GetMapping("/file-type-users")
    public ResponseEntity<List<FileTypeUserDTO>> getAllFileTypeUsers(Pageable pageable) {
        log.debug("REST request to get a page of FileTypeUsers");
        Page<FileTypeUserDTO> page = fileTypeUserService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /file-type-users/:id} : get the "id" fileTypeUser.
     *
     * @param id the id of the fileTypeUserDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the fileTypeUserDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/file-type-users/{id}")
    public ResponseEntity<FileTypeUserDTO> getFileTypeUser(@PathVariable Long id) {
        log.debug("REST request to get FileTypeUser : {}", id);
        Optional<FileTypeUserDTO> fileTypeUserDTO = fileTypeUserService.findOne(id);
        return ResponseUtil.wrapOrNotFound(fileTypeUserDTO);
    }

    /**
     * {@code DELETE  /file-type-users/:id} : delete the "id" fileTypeUser.
     *
     * @param id the id of the fileTypeUserDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/file-type-users/{id}")
    public ResponseEntity<Void> deleteFileTypeUser(@PathVariable Long id) {
        log.debug("REST request to delete FileTypeUser : {}", id);
        fileTypeUserService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    @GetMapping("/file-type-user/authority")
    public ResponseEntity<Boolean> checkAuthority(Long fileTypeId) {
        Boolean status = fileTypeUserService.searchByFileType(fileTypeId);
        return ResponseEntity.ok().body(status);
    }
}
