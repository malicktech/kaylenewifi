package com.kaylene.wifi.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kaylene.wifi.domain.Record;
import com.kaylene.wifi.repository.RecordRepository;

/**
 * Service Implementation for managing Record.
 */
@Service
@Transactional
public class RecordService {

    private final Logger log = LoggerFactory.getLogger(RecordService.class);

    private final RecordRepository recordRepository;

    public RecordService(RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    /**
     * Save a record.
     *
     * @param record the entity to save
     * @return the persisted entity
     */
    public Record save(Record record) {
        log.debug("Request to save Record : {}", record);
        return recordRepository.save(record);
    }

    /**
     * Get all the records.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Record> findAll(Pageable pageable) {
        log.debug("Request to get all Records");
        return recordRepository.findAll(pageable);
    }


    /**
     * Get one record by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Optional<Record> findOne(Long id) {
        log.debug("Request to get Record : {}", id);
        return recordRepository.findById(id);
    }

    /**
     * Delete the record by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Record : {}", id);
        recordRepository.deleteById(id);
    }

	public Optional<Record> findOneByCode(String code, Long id) {
		return recordRepository.findOneByCodeAndEventId(code, id);
	}
}
