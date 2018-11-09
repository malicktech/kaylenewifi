package com.kaylene.wifi.web.rest;

import com.kaylene.wifi.KaylenewifiApp;

import com.kaylene.wifi.domain.Record;
import com.kaylene.wifi.domain.Event;
import com.kaylene.wifi.repository.RecordRepository;
import com.kaylene.wifi.service.RecordService;
import com.kaylene.wifi.service.orangeapi.SmsService;
import com.kaylene.wifi.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;


import static com.kaylene.wifi.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the RecordResource REST controller.
 *
 * @see RecordResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = KaylenewifiApp.class)
public class RecordResourceIntTest {

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    @Autowired
    private RecordRepository recordRepository;

    @Autowired
    private RecordService recordService;
    
    @Autowired
    private SmsService smsService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restRecordMockMvc;

    private Record record;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final RecordResource recordResource = new RecordResource(recordService, smsService);
        this.restRecordMockMvc = MockMvcBuilders.standaloneSetup(recordResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Record createEntity(EntityManager em) {
        Record record = new Record()
            .phone(DEFAULT_PHONE)
            .code(DEFAULT_CODE);
        // Add required entity
        Event event = EventResourceIntTest.createEntity(em);
        em.persist(event);
        em.flush();
        record.setEvent(event);
        return record;
    }

    @Before
    public void initTest() {
        record = createEntity(em);
    }

    @Test
    @Transactional
    public void createRecord() throws Exception {
        int databaseSizeBeforeCreate = recordRepository.findAll().size();

        // Create the Record
        restRecordMockMvc.perform(post("/api/records")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(record)))
            .andExpect(status().isCreated());

        // Validate the Record in the database
        List<Record> recordList = recordRepository.findAll();
        assertThat(recordList).hasSize(databaseSizeBeforeCreate + 1);
        Record testRecord = recordList.get(recordList.size() - 1);
        assertThat(testRecord.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testRecord.getCode()).isEqualTo(DEFAULT_CODE);
    }

    @Test
    @Transactional
    public void createRecordWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = recordRepository.findAll().size();

        // Create the Record with an existing ID
        record.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restRecordMockMvc.perform(post("/api/records")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(record)))
            .andExpect(status().isBadRequest());

        // Validate the Record in the database
        List<Record> recordList = recordRepository.findAll();
        assertThat(recordList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkPhoneIsRequired() throws Exception {
        int databaseSizeBeforeTest = recordRepository.findAll().size();
        // set the field null
        record.setPhone(null);

        // Create the Record, which fails.

        restRecordMockMvc.perform(post("/api/records")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(record)))
            .andExpect(status().isBadRequest());

        List<Record> recordList = recordRepository.findAll();
        assertThat(recordList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = recordRepository.findAll().size();
        // set the field null
        record.setCode(null);

        // Create the Record, which fails.

        restRecordMockMvc.perform(post("/api/records")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(record)))
            .andExpect(status().isBadRequest());

        List<Record> recordList = recordRepository.findAll();
        assertThat(recordList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllRecords() throws Exception {
        // Initialize the database
        recordRepository.saveAndFlush(record);

        // Get all the recordList
        restRecordMockMvc.perform(get("/api/records?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(record.getId().intValue())))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE.toString())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE.toString())));
    }
    
    @Test
    @Transactional
    public void getRecord() throws Exception {
        // Initialize the database
        recordRepository.saveAndFlush(record);

        // Get the record
        restRecordMockMvc.perform(get("/api/records/{id}", record.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(record.getId().intValue()))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE.toString()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingRecord() throws Exception {
        // Get the record
        restRecordMockMvc.perform(get("/api/records/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRecord() throws Exception {
        // Initialize the database
        recordService.save(record);

        int databaseSizeBeforeUpdate = recordRepository.findAll().size();

        // Update the record
        Record updatedRecord = recordRepository.findById(record.getId()).get();
        // Disconnect from session so that the updates on updatedRecord are not directly saved in db
        em.detach(updatedRecord);
        updatedRecord
            .phone(UPDATED_PHONE)
            .code(UPDATED_CODE);

        restRecordMockMvc.perform(put("/api/records")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedRecord)))
            .andExpect(status().isOk());

        // Validate the Record in the database
        List<Record> recordList = recordRepository.findAll();
        assertThat(recordList).hasSize(databaseSizeBeforeUpdate);
        Record testRecord = recordList.get(recordList.size() - 1);
        assertThat(testRecord.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testRecord.getCode()).isEqualTo(UPDATED_CODE);
    }

    @Test
    @Transactional
    public void updateNonExistingRecord() throws Exception {
        int databaseSizeBeforeUpdate = recordRepository.findAll().size();

        // Create the Record

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRecordMockMvc.perform(put("/api/records")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(record)))
            .andExpect(status().isBadRequest());

        // Validate the Record in the database
        List<Record> recordList = recordRepository.findAll();
        assertThat(recordList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteRecord() throws Exception {
        // Initialize the database
        recordService.save(record);

        int databaseSizeBeforeDelete = recordRepository.findAll().size();

        // Get the record
        restRecordMockMvc.perform(delete("/api/records/{id}", record.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Record> recordList = recordRepository.findAll();
        assertThat(recordList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Record.class);
        Record record1 = new Record();
        record1.setId(1L);
        Record record2 = new Record();
        record2.setId(record1.getId());
        assertThat(record1).isEqualTo(record2);
        record2.setId(2L);
        assertThat(record1).isNotEqualTo(record2);
        record1.setId(null);
        assertThat(record1).isNotEqualTo(record2);
    }
}
