package com.umsa.storage.web.rest;

import com.umsa.storage.domain.Authority;
import com.umsa.storage.domain.User;
import com.umsa.storage.repository.UserRepository;
import com.umsa.storage.service.FileTypeService;
import com.umsa.storage.service.FileTypeUserService;
import com.umsa.storage.service.UserService;
import com.umsa.storage.web.rest.errors.BadRequestAlertException;
import com.umsa.storage.service.dto.FileTypeDTO;
import com.umsa.storage.service.dto.FileTypeUserDTO;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
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
    private final FileTypeService fileTypeService;
    private final UserRepository userRepository;
    private final UserService userService;

    public FileTypeUserResource(FileTypeUserService fileTypeUserService, FileTypeService fileTypeService, UserRepository userRepository, UserService userService){
        this.fileTypeUserService = fileTypeUserService;
        this.fileTypeService = fileTypeService;
        this.userRepository = userRepository;
        this.userService = userService;
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
    
    @GetMapping("/file-type-user/permissions")
    public ResponseEntity<List<FileTypeDTO>> findMyPermissions(){
        List<FileTypeUserDTO> page = fileTypeUserService.findAllMyFileTpe();
        List<FileTypeDTO> returnData = new ArrayList<>();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> currentUser = userService.getUserWithAuthoritiesByLogin(auth.getName());
        User user = currentUser.get();
        for(Authority authority: user.getAuthorities()){
            if(authority.getName().equals("ROLE_ADMIN")) {
                return ResponseEntity.ok().body(fileTypeService.findAll(new PageRequest(0, 1<<20)).getContent());
            }
        }
        for(FileTypeUserDTO file: page) {
            FileTypeDTO fileTypeDTO = fileTypeService.findOne(file.getFileTypeId()).get();
            returnData.add(fileTypeDTO);
        }
        return ResponseEntity.ok().body(returnData);        
    }
}
