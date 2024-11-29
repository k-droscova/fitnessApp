package cz.cvut.fit.tjv.fitnessApp.integration.repository;

import cz.cvut.fit.tjv.fitnessApp.domain.Instructor;
import cz.cvut.fit.tjv.fitnessApp.repository.InstructorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@ActiveProfiles("test")
@DataJpaTest
@Transactional
@AutoConfigureTestEntityManager
public class InstructorRepositoryIT {
    @Autowired
    private InstructorRepository instructorRepository;

    @Test
    void findInstructorByNameStartingWithIgnoreCase_ShouldReturnJaneWhenInputIsJa() {
        List<Instructor> result = instructorRepository.findInstructorByNameStartingWithIgnoreCase("ja");
        assertNotNull(result);
        assertEquals(1, result.size(), "Expected one match for name starting with 'ja'");
        assertEquals("Jane", result.get(0).getName(), "Expected the instructor name to be 'Jane'");
    }

    @Test
    void findInstructorByNameStartingWithIgnoreCase_ShouldReturnJohnWhenInputIsJ() {
        List<Instructor> result = instructorRepository.findInstructorByNameStartingWithIgnoreCase("j");
        assertNotNull(result);
        assertEquals(2, result.size(), "Expected two matches for name starting with 'j'");
        assertTrue(result.stream().anyMatch(i -> "John".equals(i.getName())), "Expected one of the matches to be 'John'");
        assertTrue(result.stream().anyMatch(i -> "Jane".equals(i.getName())), "Expected one of the matches to be 'Jane'");
    }

    @Test
    void findInstructorByNameStartingWithIgnoreCase_ShouldReturnEmptyListForNoMatch() {
        List<Instructor> result = instructorRepository.findInstructorByNameStartingWithIgnoreCase("z");
        assertNotNull(result);
        assertTrue(result.isEmpty(), "Expected no matches for name starting with 'z'");
    }

    @Test
    void findInstructorsBySurnameStartingWithIgnoreCase_ShouldReturnDoeWhenInputIsDo() {
        List<Instructor> result = instructorRepository.findInstructorsBySurnameStartingWithIgnoreCase("do");
        assertNotNull(result);
        assertEquals(1, result.size(), "Expected one match for surname starting with 'do'");
        assertEquals("Doe", result.get(0).getSurname(), "Expected the instructor surname to be 'Doe'");
    }

    @Test
    void findInstructorsBySurnameStartingWithIgnoreCase_ShouldReturnSmithWhenInputIsSm() {
        List<Instructor> result = instructorRepository.findInstructorsBySurnameStartingWithIgnoreCase("sm");
        assertNotNull(result);
        assertEquals(1, result.size(), "Expected one match for surname starting with 'sm'");
        assertEquals("Smith", result.get(0).getSurname(), "Expected the instructor surname to be 'Smith'");
    }

    @Test
    void findInstructorsBySurnameStartingWithIgnoreCase_ShouldReturnEmptyListForNoMatch() {
        List<Instructor> result = instructorRepository.findInstructorsBySurnameStartingWithIgnoreCase("z");
        assertNotNull(result);
        assertTrue(result.isEmpty(), "Expected no matches for surname starting with 'z'");
    }

    @Test
    void findInstructorsByNameOrSurnameStartingWithIgnoreCase_ShouldReturnJaneWhenInputIsJa() {
        List<Instructor> result = instructorRepository.findInstructorsByNameOrSurnameStartingWithIgnoreCase("ja");
        assertNotNull(result);
        assertEquals(1, result.size(), "Expected one match for name or surname starting with 'ja'");
        assertEquals("Jane", result.get(0).getName(), "Expected the instructor name to be 'Jane'");
    }

    @Test
    void findInstructorsByNameOrSurnameStartingWithIgnoreCase_ShouldReturnDoeWhenInputIsDo() {
        List<Instructor> result = instructorRepository.findInstructorsByNameOrSurnameStartingWithIgnoreCase("do");
        assertNotNull(result);
        assertEquals(1, result.size(), "Expected one match for name or surname starting with 'do'");
        assertEquals("Doe", result.get(0).getSurname(), "Expected the instructor surname to be 'Doe'");
    }

    @Test
    void findInstructorsByNameOrSurnameStartingWithIgnoreCase_ShouldReturnSmithWhenInputIsSm() {
        List<Instructor> result = instructorRepository.findInstructorsByNameOrSurnameStartingWithIgnoreCase("sm");
        assertNotNull(result);
        assertEquals(1, result.size(), "Expected one match for name or surname starting with 'sm'");
        assertEquals("Smith", result.get(0).getSurname(), "Expected the instructor surname to be 'Smith'");
    }

    @Test
    void findInstructorsByNameOrSurnameStartingWithIgnoreCase_ShouldReturnBothInstructorsWhenInputIsJ() {
        List<Instructor> result = instructorRepository.findInstructorsByNameOrSurnameStartingWithIgnoreCase("j");
        assertNotNull(result);
        assertEquals(3, result.size(), "Expected two matches for name or surname starting with 'j'");
        assertTrue(result.stream().anyMatch(i -> "John".equals(i.getName())), "Expected one of the matches to be 'John'");
        assertTrue(result.stream().anyMatch(i -> "Jane".equals(i.getName())), "Expected one of the matches to be 'Jane'");
        assertTrue(result.stream().anyMatch(i -> "Johnson".equals(i.getSurname())), "Expected one of the matches to be 'Johnson'");
    }

    @Test
    void findInstructorsByNameOrSurnameStartingWithIgnoreCase_ShouldReturnEmptyListForNoMatch() {
        List<Instructor> result = instructorRepository.findInstructorsByNameOrSurnameStartingWithIgnoreCase("z");
        assertNotNull(result);
        assertTrue(result.isEmpty(), "Expected no matches for name or surname starting with 'z'");
    }

    @Test
    void findAvailableInstructorsByOptionalClassType_ShouldReturnAllAvailableWhenClassTypeIdIsNull() {
        List<Instructor> results = instructorRepository.findAvailableInstructorsByOptionalClassType(
                null, // No classTypeId filter
                LocalDate.of(2024, 12, 1),
                LocalTime.of(10, 0)
        );

        assertNotNull(results);
        assertEquals(3, results.size(), "Expected 3 available instructors (excluding John who is booked)");
        assertTrue(results.stream().anyMatch(i -> "Jane".equals(i.getName())));
        assertTrue(results.stream().anyMatch(i -> "Alice".equals(i.getName())));
        assertTrue(results.stream().anyMatch(i -> "Bob".equals(i.getName())));
    }

    @Test
    void findAvailableInstructorsByOptionalClassType_ShouldFilterByClassType() {
        List<Instructor> results = instructorRepository.findAvailableInstructorsByOptionalClassType(
                1L, // ClassTypeId for Yoga
                LocalDate.of(2024, 12, 1),
                LocalTime.of(10, 0)
        );

        assertNotNull(results);
        assertEquals(1, results.size(), "Expected 1 available instructor specializing in Yoga (excluding John)");
        assertTrue(results.stream().anyMatch(i -> "Bob".equals(i.getName())));
    }

    @Test
    void findAvailableInstructorsByOptionalClassType_ShouldReturnAllUnbookedInstructorsWhenClassTypeIdIsNull() {
        List<Instructor> results = instructorRepository.findAvailableInstructorsByOptionalClassType(
                null, // No classTypeId filter
                LocalDate.of(2024, 12, 1),
                LocalTime.of(15, 0)
        );

        assertNotNull(results);
        assertEquals(2, results.size(), "Expected 2 available instructors (John and Bob)");
        assertTrue(results.stream().anyMatch(i -> "John".equals(i.getName())), "Expected John to be available");
        assertTrue(results.stream().anyMatch(i -> "Bob".equals(i.getName())), "Expected Bob to be available");
    }
}