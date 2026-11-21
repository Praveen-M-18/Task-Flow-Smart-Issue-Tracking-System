package com.taskflow.api.controller;

import com.taskflow.api.dto.DeveloperPerformanceReport;
import com.taskflow.api.service.DeveloperReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ContentDisposition;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * REST Controller for developer performance and accuracy reports.
 */
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final DeveloperReportService developerReportService;

    public ReportController(DeveloperReportService developerReportService) {
        this.developerReportService = developerReportService;
    }

    /**
     * GET /api/reports/developers
     * Returns developer performance data as JSON for UI consumption.
     */
    @GetMapping("/developers")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<DeveloperPerformanceReport>> getDeveloperReport() {
        return ResponseEntity.ok(developerReportService.buildPerformanceReport());
    }

    /**
     * GET /api/reports/developers/export
     * Downloads a CSV report of developer performance and accuracy metrics.
     *
     * Columns: Developer ID, Name, Email, Total Tasks Assigned,
     *          In-Progress Tasks, Completed Tasks, Completion Rate (%),
     *          Avg Resolution Time (Hours), Avg Resolution Time (Days)
     */
    @GetMapping("/developers/export")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<byte[]> exportDeveloperReportCsv() {
        String csv = developerReportService.generateCsvReport();
        byte[] csvBytes = csv.getBytes(StandardCharsets.UTF_8);

        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = "taskflow_developer_report_" + timestamp + ".csv";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
        headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());
        headers.setContentLength(csvBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(csvBytes);
    }
}
