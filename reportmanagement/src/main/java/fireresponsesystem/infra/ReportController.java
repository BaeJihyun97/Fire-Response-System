package fireresponsesystem.infra;

import fireresponsesystem.domain.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//<<< Clean Arch / Inbound Adaptor

@RestController
// @RequestMapping(value="/reports")
@Transactional
public class ReportController {

    @Autowired
    ReportRepository reportRepository;

    @RequestMapping(
        value = "reports/receivereport",
        method = RequestMethod.POST,
        produces = "application/json;charset=UTF-8"
    )
    public Report receiveReport(
        HttpServletRequest request,
        HttpServletResponse response,
        @RequestBody ReceiveReportCommand receiveReportCommand
    ) throws Exception {
        System.out.println("##### /report/receiveReport  called #####");
        Report report = new Report();
        report.receiveReport(receiveReportCommand);
        return report;
    }
    
    @RequestMapping(
        value = "reports/userId/{userId}",
        method = RequestMethod.GET,
        produces = "application/json;charset=UTF-8"
    )
    public List<Report> getReportsByUserId(
        @PathVariable("userId") String userId
    ) {
        System.out.println("##### /reports/userId/" + userId + " called #####");
        return reportRepository.findByUserId(userId);
    }
    
    @RequestMapping(
        value = "reports/latestByEvent/{eventId}",
        method = RequestMethod.GET,
        produces = "application/json;charset=UTF-8"
    )
    public Map<String, Object> getLatestReportByEventId(
        @PathVariable("eventId") Long eventId
    ) {
        System.out.println("##### /reports/latestByEvent/" + eventId + " called #####");
        
        List<Report> reports = reportRepository.findByEventId(eventId);
        Map<String, Object> result = new HashMap<>();
        
        if (reports != null && !reports.isEmpty()) {
            // 가장 최근 보고서 찾기 (uploadedAt 기준 정렬)
            Report latestReport = reports.stream()
                .sorted(Comparator.comparing(Report::getUploadedAt).reversed())
                .findFirst()
                .orElse(null);
            
            if (latestReport != null) {
                result.put("reportId", latestReport.getReportId());
                result.put("videoId", latestReport.getVideoId());
                result.put("status", latestReport.getStatus());
            }
        }
        
        return result;
    }
}
//>>> Clean Arch / Inbound Adaptor
