package com.enterpriseintellijence.enterpriseintellijence.controller;

import com.enterpriseintellijence.enterpriseintellijence.data.services.ReportService;
import com.enterpriseintellijence.enterpriseintellijence.dto.ReportDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.ReportStatus;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.enterpriseintellijence.enterpriseintellijence.security.AppSecurityConfig.SECURITY_CONFIG_NAME;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/reports", produces = "application/json")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@SecurityRequirement(name = SECURITY_CONFIG_NAME)
public class ReportController {

    private final ReportService reportService;

    @PostMapping(consumes = "application/json")
    public ResponseEntity<ReportDTO> createReport(@RequestBody ReportDTO reportDTO) {
        return ResponseEntity.ok(reportService.createReport(reportDTO));
    }

    @PostMapping(path = "/close/{id}")
    public ResponseEntity<ReportDTO> closeReport(@PathVariable String id) {
        return ResponseEntity.ok(reportService.closeReport(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReportDTO> updateReport(@PathVariable("id")String id) throws IllegalAccessException {
        return ResponseEntity.ok(reportService.updateReport(id));
    }


    @GetMapping(path = "")
    public ResponseEntity<Page<ReportDTO>> getReportsByStatus(@RequestParam ReportStatus status, @RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(reportService.getReportsByStatus(status, page, size));
    }

    @GetMapping("/working")
    public ResponseEntity<Page<ReportDTO>> getReportsMeManaging(
            @RequestParam(required = false,defaultValue = "0") int page,
            @RequestParam(required = false,defaultValue = "10")int size)throws IllegalAccessException {
        return ResponseEntity.ok(reportService.getReportsMeManaging(page,size));
    }


}
