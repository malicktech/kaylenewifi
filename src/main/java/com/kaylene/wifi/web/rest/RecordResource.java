package com.kaylene.wifi.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.kaylene.wifi.domain.Record;
import com.kaylene.wifi.service.RecordService;
import com.kaylene.wifi.service.orangeapi.OrangeSmsApiService;
import com.kaylene.wifi.web.rest.errors.BadRequestAlertException;
import com.kaylene.wifi.web.rest.errors.CodeAlreadyUsedException;
import com.kaylene.wifi.web.rest.util.HeaderUtil;
import com.kaylene.wifi.web.rest.util.PaginationUtil;

import io.github.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing Record.
 */
@RestController
@RequestMapping("/api")
public class RecordResource {

	private final Logger log = LoggerFactory.getLogger(RecordResource.class);

	private static final String ENTITY_NAME = "record";

	private final RecordService recordService;
	private final OrangeSmsApiService orangeSmsApiService;

	public RecordResource(RecordService recordService, OrangeSmsApiService orangeSmsApiService) {
		this.recordService = recordService;
		this.orangeSmsApiService = orangeSmsApiService;
	}

	/**
	 * POST /records : Create a new record.
	 *
	 * @param record
	 *            the record to create
	 * @return the ResponseEntity with status 201 (Created) and with body the new
	 *         record, or with status 400 (Bad Request) if the record has already an
	 *         ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@PostMapping("/records")
	@Timed
	public ResponseEntity<Record> createRecord(@Valid @RequestBody Record record) throws URISyntaxException {
		log.debug("REST request to save Record : {}", record);
		if (record.getId() != null) {
			throw new BadRequestAlertException("A new record cannot already have an ID", ENTITY_NAME, "idexists");
		}

		if (recordService.findOneByCode(record.getCode(), record.getEvent().getId()).isPresent()) {
			throw new CodeAlreadyUsedException();
		}

		Record result = recordService.save(record);
		if (result != null) {
			orangeSmsApiService.sendCodeSms(result);
			// TODO save status true or false if sms sendid or not
			// make a url callback to get response
		}

		return ResponseEntity.created(new URI("/api/records/" + result.getId()))
				.headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString())).body(result);
	}

	/**
	 * PUT /records : Updates an existing record.
	 *
	 * @param record
	 *            the record to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         record, or with status 400 (Bad Request) if the record is not valid,
	 *         or with status 500 (Internal Server Error) if the record couldn't be
	 *         updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@PutMapping("/records")
	@Timed
	public ResponseEntity<Record> updateRecord(@Valid @RequestBody Record record) throws URISyntaxException {
		log.debug("REST request to update Record : {}", record);
		if (record.getId() == null) {
			throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
		}
		Record result = recordService.save(record);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, record.getId().toString()))
				.body(result);
	}

	/**
	 * GET /records : get all the records.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of records in
	 *         body
	 */
	@GetMapping("/records")
	@Timed
	public ResponseEntity<List<Record>> getAllRecords(Pageable pageable) {
		log.debug("REST request to get a page of Records");
		Page<Record> page = recordService.findAll(pageable);
		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/records");
		return ResponseEntity.ok().headers(headers).body(page.getContent());
	}

	/**
	 * GET /records/:id : get the "id" record.
	 *
	 * @param id
	 *            the id of the record to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the record, or
	 *         with status 404 (Not Found)
	 */
	@GetMapping("/records/{id}")
	@Timed
	public ResponseEntity<Record> getRecord(@PathVariable Long id) {
		log.debug("REST request to get Record : {}", id);
		Optional<Record> record = recordService.findOne(id);
		return ResponseUtil.wrapOrNotFound(record);
	}

	/**
	 * DELETE /records/:id : delete the "id" record.
	 *
	 * @param id
	 *            the id of the record to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@DeleteMapping("/records/{id}")
	@Timed
	public ResponseEntity<Void> deleteRecord(@PathVariable Long id) {
		log.debug("REST request to delete Record : {}", id);
		recordService.delete(id);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
	}
}
