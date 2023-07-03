package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Report;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.ReportRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.ProductDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.ReportDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.ReportStatus;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.UserRole;
import com.enterpriseintellijence.enterpriseintellijence.security.JwtContextUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReportServiceImp implements ReportService {

    private final ReportRepository reportRepository;
    private final JwtContextUtils jwtContextUtils;

    private final ProductService productService;
    private final UserService userService;
    private final ModelMapper modelMapper;

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
        reportDTO.setLastUpdate(LocalDateTime.now());
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
        return reportRepository.findByStatusOrderByDateAsc(status, PageRequest.of(page, size)).map(this::mapToDto);
    }

    @Override
    public Page<ReportDTO> getReportsMeManaging(int page, int size) throws IllegalAccessException {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        if(loggedUser.getRole().equals(UserRole.USER))
            throw new IllegalAccessException("You can't access this page");
        return reportRepository.findByStatusAndAdminFollowedReportOrderByLastUpdateDesc(ReportStatus.PENDING,loggedUser,PageRequest.of(page,size)).map(this::mapToDto);
    }

    @Override
    public ReportDTO updateReport(String id) throws IllegalAccessException {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        if(loggedUser.getRole().equals(UserRole.USER))
            throw new IllegalAccessException("You can't modify report");

        Report report = reportRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        report.setLastUpdate(LocalDateTime.now());
        report.setAdminFollowedReport(loggedUser);
        return modelMapper.map(reportRepository.save(report),ReportDTO.class) ;
    }


    public ReportDTO mapToDto(Report report) { return modelMapper.map(report, ReportDTO.class); }

    public Report mapToEntity(ReportDTO reportDTO) { return modelMapper.map(reportDTO, Report.class); }

}
