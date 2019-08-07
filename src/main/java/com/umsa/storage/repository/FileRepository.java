package com.umsa.storage.repository;

import com.umsa.storage.domain.File;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the File entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    Page<File> findAllByFileTypeId(Pageable pageable, Long fileTypeId);
}
