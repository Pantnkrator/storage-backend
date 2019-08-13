package com.umsa.storage.repository;

import com.umsa.storage.domain.FileTypeUser;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the FileTypeUser entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FileTypeUserRepository extends JpaRepository<FileTypeUser, Long> {

    @Query("select fileTypeUser from FileTypeUser fileTypeUser where fileTypeUser.user.login = ?#{principal.username}")
    List<FileTypeUser> findByUserIsCurrentUser();

    Optional<FileTypeUser> findOneByUserIdAndFileTypeId(Long userId, Long fileTypeId);
}
