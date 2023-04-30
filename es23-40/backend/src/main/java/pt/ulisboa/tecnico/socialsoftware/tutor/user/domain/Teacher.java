package pt.ulisboa.tecnico.socialsoftware.tutor.user.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
@DiscriminatorValue(User.UserTypes.TEACHER)
public class Teacher extends User {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "teacher", orphanRemoval = true)
    private Set<TeacherDashboard> teacherDashboards = new HashSet<>();

    public Teacher() {
    }

    public Teacher(String name, String username, String email, boolean isAdmin, AuthUser.Type type) {
        super(name, username, email, Role.TEACHER, isAdmin, type);
    }

    public Teacher(String name, boolean isAdmin) {
        super(name, Role.TEACHER, isAdmin);
    }

    public Set<TeacherDashboard> getDashboards() {
        return teacherDashboards;
    }

    public void addDashboard(TeacherDashboard teacherDashboard) {
        teacherDashboards.add(teacherDashboard);
    }

    public TeacherDashboard getCourseExecutionDashboard(CourseExecution courseExecution) {
        return teacherDashboards.stream()
                .filter(dashboard -> dashboard.getCourseExecution() == courseExecution)
                .findAny()
                .orElse(null);
    }
}
