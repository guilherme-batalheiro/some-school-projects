package pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.domain.DifficultQuestion;

@Repository
@Transactional
public interface DifficultQuestionRepository extends JpaRepository<DifficultQuestion, Integer> {
}