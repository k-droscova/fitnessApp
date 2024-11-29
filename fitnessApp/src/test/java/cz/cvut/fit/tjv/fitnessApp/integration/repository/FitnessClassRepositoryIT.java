package cz.cvut.fit.tjv.fitnessApp.integration.repository;

import cz.cvut.fit.tjv.fitnessApp.domain.FitnessClass;
import cz.cvut.fit.tjv.fitnessApp.domain.Room;
import cz.cvut.fit.tjv.fitnessApp.repository.FitnessClassRepository;
import cz.cvut.fit.tjv.fitnessApp.repository.RoomRepository;
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
public class FitnessClassRepositoryIT {

    @Autowired
    private FitnessClassRepository fitnessClassRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Test
    void findFitnessClassesByDate_ReturnsMatchingClasses() {
        List<FitnessClass> results = fitnessClassRepository.findFitnessClassesByDate(LocalDate.of(2024, 12, 1));

        assertNotNull(results);
        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(fc -> fc.getTime().equals(LocalTime.of(10, 0))));
        assertTrue(results.stream().anyMatch(fc -> fc.getTime().equals(LocalTime.of(15, 0))));
    }

    @Test
    void findFitnessClassesByDate_ReturnsEmpty_WhenNoMatches() {
        List<FitnessClass> results = fitnessClassRepository.findFitnessClassesByDate(LocalDate.of(2024, 12, 3));

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    void findFitnessClassesByDateAndRoomId_ReturnsMatchingClasses() {
        Room room1 = roomRepository.findById(1L).orElseThrow(() -> new IllegalStateException("Room not found"));
        List<FitnessClass> results = fitnessClassRepository.findFitnessClassesByDateAndRoom_Id(LocalDate.of(2024, 12, 1), room1.getId());

        assertNotNull(results);
        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(fc -> fc.getTime().equals(LocalTime.of(10, 0))));
        assertTrue(results.stream().anyMatch(fc -> fc.getTime().equals(LocalTime.of(15, 0))));
    }

    @Test
    void findFitnessClassesByDateAndRoomId_ReturnsEmpty_WhenNoMatches() {
        Room room2 = roomRepository.findById(2L).orElseThrow(() -> new IllegalStateException("Room not found"));
        List<FitnessClass> results = fitnessClassRepository.findFitnessClassesByDateAndRoom_Id(LocalDate.of(2024, 12, 1), room2.getId());

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    void findFitnessClassesByTimeBetweenAndDate_ReturnsMatchingClasses() {
        List<FitnessClass> results = fitnessClassRepository.findFitnessClassesByTimeBetweenAndDate(
                LocalTime.of(9, 0),
                LocalTime.of(11, 0),
                LocalDate.of(2024, 12, 1)
        );

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(LocalTime.of(10, 0), results.get(0).getTime());
    }

    @Test
    void findFitnessClassesByTimeBetweenAndDate_ReturnsEmpty_WhenNoMatches() {
        List<FitnessClass> results = fitnessClassRepository.findFitnessClassesByTimeBetweenAndDate(
                LocalTime.of(16, 0),
                LocalTime.of(18, 0),
                LocalDate.of(2024, 12, 1)
        );

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }
}