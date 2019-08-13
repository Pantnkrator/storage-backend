package com.umsa.storage.web.rest;

import com.umsa.storage.StorageApp;
import com.umsa.storage.domain.FileTypeUser;
import com.umsa.storage.repository.FileTypeUserRepository;
import com.umsa.storage.service.FileTypeUserService;
import com.umsa.storage.service.dto.FileTypeUserDTO;
import com.umsa.storage.service.mapper.FileTypeUserMapper;
import com.umsa.storage.web.rest.errors.ExceptionTranslator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.umsa.storage.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link FileTypeUserResource} REST controller.
 */
@SpringBootTest(classes = StorageApp.class)
public class FileTypeUserResourceIT {

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    private static final Instant SMALLER_CREATED_DATE = Instant.ofEpochMilli(-1L);

    private static final String DEFAULT_LAST_MODIFIED_BY = "AAAAAAAAAA";
    private static final String UPDATED_LAST_MODIFIED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_LAST_MODIFIED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_MODIFIED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    private static final Instant SMALLER_LAST_MODIFIED_DATE = Instant.ofEpochMilli(-1L);

    @Autowired
    private FileTypeUserRepository fileTypeUserRepository;

    @Autowired
    private FileTypeUserMapper fileTypeUserMapper;

    @Autowired
    private FileTypeUserService fileTypeUserService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restFileTypeUserMockMvc;

    private FileTypeUser fileTypeUser;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final FileTypeUserResource fileTypeUserResource = new FileTypeUserResource(fileTypeUserService);
        this.restFileTypeUserMockMvc = MockMvcBuilders.standaloneSetup(fileTypeUserResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FileTypeUser createEntity(EntityManager em) {
        FileTypeUser fileTypeUser = new FileTypeUser()
            .createdBy(DEFAULT_CREATED_BY)
            .createdDate(DEFAULT_CREATED_DATE)
            .lastModifiedBy(DEFAULT_LAST_MODIFIED_BY)
            .lastModifiedDate(DEFAULT_LAST_MODIFIED_DATE);
        return fileTypeUser;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FileTypeUser createUpdatedEntity(EntityManager em) {
        FileTypeUser fileTypeUser = new FileTypeUser()
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);
        return fileTypeUser;
    }

    @BeforeEach
    public void initTest() {
        fileTypeUser = createEntity(em);
    }

    @Test
    @Transactional
    public void createFileTypeUser() throws Exception {
        int databaseSizeBeforeCreate = fileTypeUserRepository.findAll().size();

        // Create the FileTypeUser
        FileTypeUserDTO fileTypeUserDTO = fileTypeUserMapper.toDto(fileTypeUser);
        restFileTypeUserMockMvc.perform(post("/api/file-type-users")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(fileTypeUserDTO)))
            .andExpect(status().isCreated());

        // Validate the FileTypeUser in the database
        List<FileTypeUser> fileTypeUserList = fileTypeUserRepository.findAll();
        assertThat(fileTypeUserList).hasSize(databaseSizeBeforeCreate + 1);
        FileTypeUser testFileTypeUser = fileTypeUserList.get(fileTypeUserList.size() - 1);
        assertThat(testFileTypeUser.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testFileTypeUser.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testFileTypeUser.getLastModifiedBy()).isEqualTo(DEFAULT_LAST_MODIFIED_BY);
        assertThat(testFileTypeUser.getLastModifiedDate()).isEqualTo(DEFAULT_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    public void createFileTypeUserWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = fileTypeUserRepository.findAll().size();

        // Create the FileTypeUser with an existing ID
        fileTypeUser.setId(1L);
        FileTypeUserDTO fileTypeUserDTO = fileTypeUserMapper.toDto(fileTypeUser);

        // An entity with an existing ID cannot be created, so this API call must fail
        restFileTypeUserMockMvc.perform(post("/api/file-type-users")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(fileTypeUserDTO)))
            .andExpect(status().isBadRequest());

        // Validate the FileTypeUser in the database
        List<FileTypeUser> fileTypeUserList = fileTypeUserRepository.findAll();
        assertThat(fileTypeUserList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllFileTypeUsers() throws Exception {
        // Initialize the database
        fileTypeUserRepository.saveAndFlush(fileTypeUser);

        // Get all the fileTypeUserList
        restFileTypeUserMockMvc.perform(get("/api/file-type-users?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(fileTypeUser.getId().intValue())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY.toString())))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())));
    }
    
    @Test
    @Transactional
    public void getFileTypeUser() throws Exception {
        // Initialize the database
        fileTypeUserRepository.saveAndFlush(fileTypeUser);

        // Get the fileTypeUser
        restFileTypeUserMockMvc.perform(get("/api/file-type-users/{id}", fileTypeUser.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(fileTypeUser.getId().intValue()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY.toString()))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY.toString()))
            .andExpect(jsonPath("$.lastModifiedDate").value(DEFAULT_LAST_MODIFIED_DATE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingFileTypeUser() throws Exception {
        // Get the fileTypeUser
        restFileTypeUserMockMvc.perform(get("/api/file-type-users/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateFileTypeUser() throws Exception {
        // Initialize the database
        fileTypeUserRepository.saveAndFlush(fileTypeUser);

        int databaseSizeBeforeUpdate = fileTypeUserRepository.findAll().size();

        // Update the fileTypeUser
        FileTypeUser updatedFileTypeUser = fileTypeUserRepository.findById(fileTypeUser.getId()).get();
        // Disconnect from session so that the updates on updatedFileTypeUser are not directly saved in db
        em.detach(updatedFileTypeUser);
        updatedFileTypeUser
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);
        FileTypeUserDTO fileTypeUserDTO = fileTypeUserMapper.toDto(updatedFileTypeUser);

        restFileTypeUserMockMvc.perform(put("/api/file-type-users")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(fileTypeUserDTO)))
            .andExpect(status().isOk());

        // Validate the FileTypeUser in the database
        List<FileTypeUser> fileTypeUserList = fileTypeUserRepository.findAll();
        assertThat(fileTypeUserList).hasSize(databaseSizeBeforeUpdate);
        FileTypeUser testFileTypeUser = fileTypeUserList.get(fileTypeUserList.size() - 1);
        assertThat(testFileTypeUser.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testFileTypeUser.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testFileTypeUser.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
        assertThat(testFileTypeUser.getLastModifiedDate()).isEqualTo(UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    public void updateNonExistingFileTypeUser() throws Exception {
        int databaseSizeBeforeUpdate = fileTypeUserRepository.findAll().size();

        // Create the FileTypeUser
        FileTypeUserDTO fileTypeUserDTO = fileTypeUserMapper.toDto(fileTypeUser);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFileTypeUserMockMvc.perform(put("/api/file-type-users")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(fileTypeUserDTO)))
            .andExpect(status().isBadRequest());

        // Validate the FileTypeUser in the database
        List<FileTypeUser> fileTypeUserList = fileTypeUserRepository.findAll();
        assertThat(fileTypeUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteFileTypeUser() throws Exception {
        // Initialize the database
        fileTypeUserRepository.saveAndFlush(fileTypeUser);

        int databaseSizeBeforeDelete = fileTypeUserRepository.findAll().size();

        // Delete the fileTypeUser
        restFileTypeUserMockMvc.perform(delete("/api/file-type-users/{id}", fileTypeUser.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<FileTypeUser> fileTypeUserList = fileTypeUserRepository.findAll();
        assertThat(fileTypeUserList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FileTypeUser.class);
        FileTypeUser fileTypeUser1 = new FileTypeUser();
        fileTypeUser1.setId(1L);
        FileTypeUser fileTypeUser2 = new FileTypeUser();
        fileTypeUser2.setId(fileTypeUser1.getId());
        assertThat(fileTypeUser1).isEqualTo(fileTypeUser2);
        fileTypeUser2.setId(2L);
        assertThat(fileTypeUser1).isNotEqualTo(fileTypeUser2);
        fileTypeUser1.setId(null);
        assertThat(fileTypeUser1).isNotEqualTo(fileTypeUser2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(FileTypeUserDTO.class);
        FileTypeUserDTO fileTypeUserDTO1 = new FileTypeUserDTO();
        fileTypeUserDTO1.setId(1L);
        FileTypeUserDTO fileTypeUserDTO2 = new FileTypeUserDTO();
        assertThat(fileTypeUserDTO1).isNotEqualTo(fileTypeUserDTO2);
        fileTypeUserDTO2.setId(fileTypeUserDTO1.getId());
        assertThat(fileTypeUserDTO1).isEqualTo(fileTypeUserDTO2);
        fileTypeUserDTO2.setId(2L);
        assertThat(fileTypeUserDTO1).isNotEqualTo(fileTypeUserDTO2);
        fileTypeUserDTO1.setId(null);
        assertThat(fileTypeUserDTO1).isNotEqualTo(fileTypeUserDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(fileTypeUserMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(fileTypeUserMapper.fromId(null)).isNull();
    }
}
