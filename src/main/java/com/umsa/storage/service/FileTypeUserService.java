package com.umsa.storage.service;

import com.umsa.storage.domain.Authority;
import com.umsa.storage.domain.FileTypeUser;
import com.umsa.storage.domain.User;
import com.umsa.storage.repository.FileTypeRepository;
import com.umsa.storage.repository.FileTypeUserRepository;
import com.umsa.storage.repository.UserRepository;
import com.umsa.storage.service.dto.FileTypeUserDTO;
import com.umsa.storage.service.mapper.FileTypeUserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing {@link FileTypeUser}.
 */
@Service
@Transactional
public class FileTypeUserService {

    private final Logger log = LoggerFactory.getLogger(FileTypeUserService.class);

    private final FileTypeUserRepository fileTypeUserRepository;

    private final FileTypeUserMapper fileTypeUserMapper;

    private final UserRepository userRepository;

    private final UserService userService;

    public FileTypeUserService(FileTypeUserRepository fileTypeUserRepository, FileTypeUserMapper fileTypeUserMapper, UserRepository userRepository, UserService userService) {
        this.fileTypeUserRepository = fileTypeUserRepository;
        this.fileTypeUserMapper = fileTypeUserMapper;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    /**
     * Save a fileTypeUser.
     *
     * @param fileTypeUserDTO the entity to save.
     * @return the persisted entity.
     */
    public FileTypeUserDTO save(FileTypeUserDTO fileTypeUserDTO) {
        log.debug("Request to save FileTypeUser : {}", fileTypeUserDTO);
        FileTypeUser fileTypeUser = fileTypeUserMapper.toEntity(fileTypeUserDTO);
        fileTypeUser = fileTypeUserRepository.save(fileTypeUser);
        return fileTypeUserMapper.toDto(fileTypeUser);
    }

    /**
     * Get all the fileTypeUsers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<FileTypeUserDTO> findAll(Pageable pageable) {
        log.debug("Request to get all FileTypeUsers");
        return fileTypeUserRepository.findAll(pageable)
            .map(fileTypeUserMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<FileTypeUserDTO> findAllMyFileTpe() {
        log.debug("Request to get all FileTypeUsers");
        return fileTypeUserMapper.toDto(fileTypeUserRepository.findByUserIsCurrentUser());
    }




    /**
     * Get one fileTypeUser by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<FileTypeUserDTO> findOne(Long id) {
        log.debug("Request to get FileTypeUser : {}", id);
        return fileTypeUserRepository.findById(id)
            .map(fileTypeUserMapper::toDto);
    }

    /**
     * Delete the fileTypeUser by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete FileTypeUser : {}", id);
        fileTypeUserRepository.deleteById(id);
    }

    public Boolean searchByFileType(Long fileTypeId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> currentUser = userService.getUserWithAuthoritiesByLogin(auth.getName());
        User user = currentUser.get();
        for(Authority authority: user.getAuthorities()){
            if(authority.getName().equals("ROLE_ADMIN")) {
                return true;
            }
        }
        Optional<FileTypeUser> fileTypeUser = fileTypeUserRepository.findOneByUserIdAndFileTypeId(user.getId(), fileTypeId);
        if(fileTypeUser.isPresent()) {
            return true;
        } else {
            return false;
        }
    }


    public void audit(FileTypeUser pago) {
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
