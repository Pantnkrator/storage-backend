package com.umsa.storage.web.rest;

import com.umsa.storage.service.FileTypeService;
import com.umsa.storage.web.rest.errors.BadRequestAlertException;
import com.umsa.storage.service.dto.FileTypeDTO;

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
 * REST controller for managing {@link com.umsa.storage.domain.FileType}.
 */
@RestController
@RequestMapping("/api")
public class FileTypeResource {

    private final Logger log = LoggerFactory.getLogger(FileTypeResource.class);

    private static final String ENTITY_NAME = "fileType";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FileTypeService fileTypeService;

    public FileTypeResource(FileTypeService fileTypeService) {
        this.fileTypeService = fileTypeService;
    }

    /**
     * {@code POST  /file-types} : Create a new fileType.
     *
     * @param fileTypeDTO the fileTypeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new fileTypeDTO, or with status {@code 400 (Bad Request)} if the fileType has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/file-types")
    public ResponseEntity<FileTypeDTO> createFileType(@RequestBody FileTypeDTO fileTypeDTO) throws URISyntaxException {
        log.debug("REST request to save FileType : {}", fileTypeDTO);
        if (fileTypeDTO.getId() != null) {
            throw new BadRequestAlertException("A new fileType cannot already have an ID", ENTITY_NAME, "idexists");
        }
        FileTypeDTO result = fileTypeService.save(fileTypeDTO);
        return ResponseEntity.created(new URI("/api/file-types/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /file-types} : Updates an existing fileType.
     *
     * @param fileTypeDTO the fileTypeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated fileTypeDTO,
     * or with status {@code 400 (Bad Request)} if the fileTypeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the fileTypeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/file-types")
    public ResponseEntity<FileTypeDTO> updateFileType(@RequestBody FileTypeDTO fileTypeDTO) throws URISyntaxException {
        log.debug("REST request to update FileType : {}", fileTypeDTO);
        if (fileTypeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        FileTypeDTO result = fileTypeService.save(fileTypeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, fileTypeDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /file-types} : get all the fileTypes.
     *

     * @param pageable the pagination information.

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of fileTypes in body.
     */
    @GetMapping("/file-types")
    public ResponseEntity<List<FileTypeDTO>> getAllFileTypes(Pageable pageable) {
        log.debug("REST request to get a page of FileTypes");
        Page<FileTypeDTO> page = fileTypeService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /file-types/:id} : get the "id" fileType.
     *
     * @param id the id of the fileTypeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the fileTypeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/file-types/{id}")
    public ResponseEntity<FileTypeDTO> getFileType(@PathVariable Long id) {
        log.debug("REST request to get FileType : {}", id);
        Optional<FileTypeDTO> fileTypeDTO = fileTypeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(fileTypeDTO);
    }

    /**
     * {@code DELETE  /file-types/:id} : delete the "id" fileType.
     *
     * @param id the id of the fileTypeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/file-types/{id}")
    public ResponseEntity<Void> deleteFileType(@PathVariable Long id) {
        log.debug("REST request to delete FileType : {}", id);
        fileTypeService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
}
