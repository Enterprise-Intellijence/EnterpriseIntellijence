package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Report;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.ReportRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.ProductDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.ReportDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.ReportStatus;
import com.enterpriseintellijence.enterpriseintellijence.security.JwtContextUtils;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReportServiceImp implements ReportService {

    private static ReportRepository reportRepository;
    private static JwtContextUtils jwtContextUtils;

    private static ProductService productService;
    private static UserService userService;
    private static ModelMapper modelMapper;

    @Override
    public ReportDTO createReport(ReportDTO reportDTO) {

        if (!jwtContextUtils.getUserLoggedFromContext().getId().equals(reportDTO.getReporterUser().getId())
                 || userService.findUserById(reportDTO.getReportedUser().getId()) == null) {
            throw new RuntimeException("User not allowed to report");
        }

        if (reportDTO.getReportedProduct() != null) {
            ProductDTO productDTO = productService.getProductById(reportDTO.getReportedProduct().getId(), true);
            if (productDTO == null) {
                throw new RuntimeException("Product not found");
            }
        }
        reportDTO.setStatus(ReportStatus.PENDING);
        reportDTO.setDate(LocalDateTime.now());
        Report report = mapToEntity(reportDTO);

        report = reportRepository.save(report);
        return mapToDto(report);
    }

    @Override
    public ReportDTO closeReport(String reportId) {
        Report report = reportRepository.findById(reportId).orElseThrow(() -> new RuntimeException("Report not found"));
        if(reportRepository.findById(reportId).isEmpty()) {
            throw new RuntimeException("Report not found");
        }
        report.setStatus(ReportStatus.CLOSED);
        report = reportRepository.save(report);
        return mapToDto(report);
    }

    @Override
    public Page<ReportDTO> getReportsByStatus(ReportStatus status, int page, int size) {
        return reportRepository.findByStatus(status, PageRequest.of(page, size)).map(this::mapToDto);
    }



    public ReportDTO mapToDto(Report report) { return modelMapper.map(report, ReportDTO.class); }

    public Report mapToEntity(ReportDTO reportDTO) { return modelMapper.map(reportDTO, Report.class); }

}
