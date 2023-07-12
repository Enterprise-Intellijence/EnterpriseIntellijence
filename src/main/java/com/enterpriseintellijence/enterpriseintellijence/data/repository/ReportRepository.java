package com.enterpriseintellijence.enterpriseintellijence.data.repository;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Report;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report,String>, JpaSpecificationExecutor<Report> {
    Page<Report> findAll(Pageable pageable);

    Page<Report> findByStatusOrderByDateAsc(ReportStatus status, Pageable pageable);
    Page<Report> findByStatusAndAdminFollowedReportOrderByLastUpdateDesc(ReportStatus status, User user, Pageable pageable);
}
