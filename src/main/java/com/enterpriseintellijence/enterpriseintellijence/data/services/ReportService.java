package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.dto.ReportDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.ReportStatus;
import org.springframework.data.domain.Page;

public interface ReportService {

    ReportDTO createReport(ReportDTO reportDTO);

    ReportDTO closeReport(String reportId);

    Page<ReportDTO> getReportsByStatus(ReportStatus status, int page, int size);

    Page<ReportDTO> getReportsMeManaging(int page, int size) throws IllegalAccessException;
}
