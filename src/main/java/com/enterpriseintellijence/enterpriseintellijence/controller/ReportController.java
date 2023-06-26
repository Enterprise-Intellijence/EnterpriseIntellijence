package com.enterpriseintellijence.enterpriseintellijence.controller;

import com.enterpriseintellijence.enterpriseintellijence.data.services.ReportService;
import com.enterpriseintellijence.enterpriseintellijence.dto.ReportDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.ReportStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/reports", produces = "application/json")
@CrossOrigin(origins = "*", allowedHeaders = "*")
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
