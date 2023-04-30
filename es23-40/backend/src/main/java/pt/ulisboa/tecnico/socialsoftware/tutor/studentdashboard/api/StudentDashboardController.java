package pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.dto.StudentDashboardDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.dto.StatsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.services.StudentDashboardService;

import java.security.Principal;

@RestController
public class StudentDashboardController {
    @Autowired
    private StudentDashboardService studentDashboardService;

    StudentDashboardController(StudentDashboardService studentDashboardService) {
        this.studentDashboardService = studentDashboardService;
    }

    @GetMapping("/students/dashboards/executions/{courseExecutionId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#courseExecutionId, 'EXECUTION.ACCESS')")
    public StudentDashboardDto getStudentDashboard(Principal principal, @PathVariable int courseExecutionId) {
        int studentId = ((AuthUser) ((Authentication) principal).getPrincipal()).getUser().getId();

        return studentDashboardService.getStudentDashboard(courseExecutionId, studentId);
    }

    @GetMapping("/students/dashboards/{dashboardId}/stats")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#dashboardId, 'DASHBOARD.ACCESS')")
    public StatsDto getStats(@PathVariable int dashboardId) {
        return studentDashboardService.getStats(dashboardId);
    }

}
