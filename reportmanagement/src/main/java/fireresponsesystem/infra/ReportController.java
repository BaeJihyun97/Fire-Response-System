package fireresponsesystem.infra;

import fireresponsesystem.domain.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import java.util.Optional;
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
        value = "reports//receivereport",
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
}
//>>> Clean Arch / Inbound Adaptor
