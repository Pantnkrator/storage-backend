package com.umsa.storage.web.rest;

import com.umsa.storage.StorageApp;
import com.umsa.storage.domain.FileType;
import com.umsa.storage.repository.FileTypeRepository;
import com.umsa.storage.service.FileTypeService;
import com.umsa.storage.service.dto.FileTypeDTO;
import com.umsa.storage.service.mapper.FileTypeMapper;
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
 * Integration tests for the {@link FileTypeResource} REST controller.
 */
@SpringBootTest(classes = StorageApp.class)
public class FileTypeResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

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
    private FileTypeRepository fileTypeRepository;

    @Autowired
    private FileTypeMapper fileTypeMapper;

    @Autowired
    private FileTypeService fileTypeService;

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

    private MockMvc restFileTypeMockMvc;

    private FileType fileType;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final FileTypeResource fileTypeResource = new FileTypeResource(fileTypeService);
        this.restFileTypeMockMvc = MockMvcBuilders.standaloneSetup(fileTypeResource)
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
    public static FileType createEntity(EntityManager em) {
        FileType fileType = new FileType()
            .name(DEFAULT_NAME)
            .createdBy(DEFAULT_CREATED_BY)
            .createdDate(DEFAULT_CREATED_DATE)
            .lastModifiedBy(DEFAULT_LAST_MODIFIED_BY)
            .lastModifiedDate(DEFAULT_LAST_MODIFIED_DATE);
        return fileType;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FileType createUpdatedEntity(EntityManager em) {
        FileType fileType = new FileType()
            .name(UPDATED_NAME)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);
        return fileType;
    }

    @BeforeEach
    public void initTest() {
        fileType = createEntity(em);
    }

    @Test
    @Transactional
    public void createFileType() throws Exception {
        int databaseSizeBeforeCreate = fileTypeRepository.findAll().size();

        // Create the FileType
        FileTypeDTO fileTypeDTO = fileTypeMapper.toDto(fileType);
        restFileTypeMockMvc.perform(post("/api/file-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(fileTypeDTO)))
            .andExpect(status().isCreated());

        // Validate the FileType in the database
        List<FileType> fileTypeList = fileTypeRepository.findAll();
        assertThat(fileTypeList).hasSize(databaseSizeBeforeCreate + 1);
        FileType testFileType = fileTypeList.get(fileTypeList.size() - 1);
        assertThat(testFileType.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testFileType.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testFileType.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testFileType.getLastModifiedBy()).isEqualTo(DEFAULT_LAST_MODIFIED_BY);
        assertThat(testFileType.getLastModifiedDate()).isEqualTo(DEFAULT_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    public void createFileTypeWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = fileTypeRepository.findAll().size();

        // Create the FileType with an existing ID
        fileType.setId(1L);
        FileTypeDTO fileTypeDTO = fileTypeMapper.toDto(fileType);

        // An entity with an existing ID cannot be created, so this API call must fail
        restFileTypeMockMvc.perform(post("/api/file-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(fileTypeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the FileType in the database
        List<FileType> fileTypeList = fileTypeRepository.findAll();
        assertThat(fileTypeList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllFileTypes() throws Exception {
        // Initialize the database
        fileTypeRepository.saveAndFlush(fileType);

        // Get all the fileTypeList
        restFileTypeMockMvc.perform(get("/api/file-types?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(fileType.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY.toString())))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())));
    }
    
    @Test
    @Transactional
    public void getFileType() throws Exception {
        // Initialize the database
        fileTypeRepository.saveAndFlush(fileType);

        // Get the fileType
        restFileTypeMockMvc.perform(get("/api/file-types/{id}", fileType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(fileType.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY.toString()))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY.toString()))
            .andExpect(jsonPath("$.lastModifiedDate").value(DEFAULT_LAST_MODIFIED_DATE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingFileType() throws Exception {
        // Get the fileType
        restFileTypeMockMvc.perform(get("/api/file-types/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateFileType() throws Exception {
        // Initialize the database
        fileTypeRepository.saveAndFlush(fileType);

        int databaseSizeBeforeUpdate = fileTypeRepository.findAll().size();

        // Update the fileType
        FileType updatedFileType = fileTypeRepository.findById(fileType.getId()).get();
        // Disconnect from session so that the updates on updatedFileType are not directly saved in db
        em.detach(updatedFileType);
        updatedFileType
            .name(UPDATED_NAME)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);
        FileTypeDTO fileTypeDTO = fileTypeMapper.toDto(updatedFileType);

        restFileTypeMockMvc.perform(put("/api/file-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(fileTypeDTO)))
            .andExpect(status().isOk());

        // Validate the FileType in the database
        List<FileType> fileTypeList = fileTypeRepository.findAll();
        assertThat(fileTypeList).hasSize(databaseSizeBeforeUpdate);
        FileType testFileType = fileTypeList.get(fileTypeList.size() - 1);
        assertThat(testFileType.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testFileType.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testFileType.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testFileType.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
        assertThat(testFileType.getLastModifiedDate()).isEqualTo(UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    public void updateNonExistingFileType() throws Exception {
        int databaseSizeBeforeUpdate = fileTypeRepository.findAll().size();

        // Create the FileType
        FileTypeDTO fileTypeDTO = fileTypeMapper.toDto(fileType);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFileTypeMockMvc.perform(put("/api/file-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(fileTypeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the FileType in the database
        List<FileType> fileTypeList = fileTypeRepository.findAll();
        assertThat(fileTypeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteFileType() throws Exception {
        // Initialize the database
        fileTypeRepository.saveAndFlush(fileType);

        int databaseSizeBeforeDelete = fileTypeRepository.findAll().size();

        // Delete the fileType
        restFileTypeMockMvc.perform(delete("/api/file-types/{id}", fileType.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<FileType> fileTypeList = fileTypeRepository.findAll();
        assertThat(fileTypeList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FileType.class);
        FileType fileType1 = new FileType();
        fileType1.setId(1L);
        FileType fileType2 = new FileType();
        fileType2.setId(fileType1.getId());
        assertThat(fileType1).isEqualTo(fileType2);
        fileType2.setId(2L);
        assertThat(fileType1).isNotEqualTo(fileType2);
        fileType1.setId(null);
        assertThat(fileType1).isNotEqualTo(fileType2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(FileTypeDTO.class);
        FileTypeDTO fileTypeDTO1 = new FileTypeDTO();
        fileTypeDTO1.setId(1L);
        FileTypeDTO fileTypeDTO2 = new FileTypeDTO();
        assertThat(fileTypeDTO1).isNotEqualTo(fileTypeDTO2);
        fileTypeDTO2.setId(fileTypeDTO1.getId());
        assertThat(fileTypeDTO1).isEqualTo(fileTypeDTO2);
        fileTypeDTO2.setId(2L);
        assertThat(fileTypeDTO1).isNotEqualTo(fileTypeDTO2);
        fileTypeDTO1.setId(null);
        assertThat(fileTypeDTO1).isNotEqualTo(fileTypeDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(fileTypeMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(fileTypeMapper.fromId(null)).isNull();
    }
}
