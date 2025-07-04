package cz.cvut.fit.tjv.fitnessApp.integration.repository;

import cz.cvut.fit.tjv.fitnessApp.domain.Trainee;
import cz.cvut.fit.tjv.fitnessApp.repository.TraineeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
@Transactional
@AutoConfigureTestEntityManager
public class TraineeRepositoryIT {

    @Autowired
    private TraineeRepository traineeRepository;

    @Test
    void findByFitnessClassId_ShouldReturnTraineesForClass() {
        List<Trainee> results = traineeRepository.findByFitnessClassId(1L); // Yoga class

        assertNotNull(results);
        assertEquals(3, results.size(), "Expected 3 trainees enrolled in the Yoga class (Class ID 1)");
        assertTrue(results.stream().anyMatch(t -> "Alice".equals(t.getName())));
        assertTrue(results.stream().anyMatch(t -> "Bob".equals(t.getName())));
        assertTrue(results.stream().anyMatch(t -> "Carol".equals(t.getName())));
    }

    @Test
    void findByFitnessClassId_ShouldReturnEmptyListWhenNoTraineesEnrolled() {
        List<Trainee> results = traineeRepository.findByFitnessClassId(5L); // Spin class

        assertNotNull(results);
        assertTrue(results.isEmpty(), "Expected no trainees enrolled in the Spin class (Class ID 5)");
    }

    @Test
    void findByFitnessClassId_ShouldReturnEmptyListForInvalidClassId() {
        List<Trainee> results = traineeRepository.findByFitnessClassId(999L); // Non-existent class

        assertNotNull(results);
        assertTrue(results.isEmpty(), "Expected no trainees for a non-existent class (Class ID 999)");
    }

    @Test
    void findByFitnessClassId_ShouldReturnTraineesForAnotherClass() {
        List<Trainee> results = traineeRepository.findByFitnessClassId(3L); // Zumba class

        assertNotNull(results);
        assertEquals(3, results.size(), "Expected 3 trainees enrolled in the Zumba class (Class ID 3)");
        assertTrue(results.stream().anyMatch(t -> "Alice".equals(t.getName())));
        assertTrue(results.stream().anyMatch(t -> "Bob".equals(t.getName())));
        assertTrue(results.stream().anyMatch(t -> "Dave".equals(t.getName())));
    }

    @Test
    void findByNameOrSurnameStartingWithIgnoreCase_ShouldReturnTraineesByPartialMatch() {
        List<Trainee> results = traineeRepository.findByNameOrSurnameStartingWithIgnoreCase("b");

        assertNotNull(results);
        assertEquals(2, results.size(), "Expected 2 trainees with names or surnames starting with 'b'");
        assertTrue(results.stream().anyMatch(t -> "Alice".equals(t.getName()) && "Brown".equals(t.getSurname())),
                "Expected trainee Alice Brown to be in the results");
        assertTrue(results.stream().anyMatch(t -> "Bob".equals(t.getName()) && "White".equals(t.getSurname())),
                "Expected trainee Bob White to be in the results");
    }

    @Test
    void findByNameOrSurnameStartingWithIgnoreCase_ShouldReturnEmptyForNonMatchingInput() {
        List<Trainee> results = traineeRepository.findByNameOrSurnameStartingWithIgnoreCase("z");

        assertNotNull(results);
        assertTrue(results.isEmpty(), "Expected no trainees with names or surnames starting with 'z'");
    }

    @Test
    void findByNameOrSurnameStartingWithIgnoreCase_ShouldHandleCaseInsensitiveSearch() {
        List<Trainee> results = traineeRepository.findByNameOrSurnameStartingWithIgnoreCase("B");

        assertNotNull(results);
        assertEquals(2, results.size(), "Expected 2 trainees with names or surnames starting with 'B' (case insensitive)");
        assertTrue(results.stream().anyMatch(t -> "Alice".equals(t.getName()) && "Brown".equals(t.getSurname())),
                "Expected trainee Alice Brown to be in the results");
        assertTrue(results.stream().anyMatch(t -> "Bob".equals(t.getName()) && "White".equals(t.getSurname())),
                "Expected trainee Bob White to be in the results");
    }
}