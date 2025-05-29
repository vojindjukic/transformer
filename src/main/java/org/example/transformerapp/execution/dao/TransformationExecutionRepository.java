package org.example.transformerapp.execution.dao;


import org.example.transformerapp.execution.model.TransformationExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransformationExecutionRepository extends JpaRepository<TransformationExecution, Long> {

    List<TransformationExecution> findByTimestampBetween(
            LocalDateTime from, LocalDateTime to);

}
