package com.kaylene.wifi.repository;

import com.kaylene.wifi.domain.Record;

import java.util.Optional;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Record entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {

	Optional<Record> findOneByCodeAndEventId(String code, Long id);

}
