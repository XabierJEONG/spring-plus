package org.example.expert.domain.log.repository;

import org.example.expert.domain.log.entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface LogRepository extends JpaRepository<Log, Long> {
}
