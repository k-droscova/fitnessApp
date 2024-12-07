package cz.cvut.fit.tjv.fitnessApp.integration.repository;

import cz.cvut.fit.tjv.fitnessApp.domain.Room;
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
public class RoomRepositoryIT {

    @Autowired
    private RoomRepository roomRepository;

    @Test
    void findAvailableRoomsByOptionalClassType_ShouldReturnAllAvailableWhenClassTypeIdIsNull() {
        List<Room> results = roomRepository.findAvailableRoomsByOptionalClassType(
                null, // No classTypeId filter
                LocalDate.of(2024, 12, 1),
                LocalTime.of(12, 0) // Unoccupied time
        );

        assertNotNull(results);
        assertEquals(5, results.size(), "Expected all 5 rooms to be available");
        assertTrue(results.stream().anyMatch(r -> r.getId() == 1L));
        assertTrue(results.stream().anyMatch(r -> r.getId() == 2L));
        assertTrue(results.stream().anyMatch(r -> r.getId() == 3L));
        assertTrue(results.stream().anyMatch(r -> r.getId() == 4L));
        assertTrue(results.stream().anyMatch(r -> r.getId() == 5L));
    }

    @Test
    void findAvailableRoomsByOptionalClassType_ShouldFilterByClassType() {
        List<Room> results = roomRepository.findAvailableRoomsByOptionalClassType(
                1L, // ClassTypeId for Yoga
                LocalDate.of(2024, 12, 1),
                LocalTime.of(12, 0) // Unoccupied time
        );

        assertNotNull(results);
        assertEquals(3, results.size(), "Expected 3 rooms specialized for Yoga to be available");
        assertTrue(results.stream().anyMatch(r -> r.getId() == 1L));
        assertTrue(results.stream().anyMatch(r -> r.getId() == 3L));
        assertTrue(results.stream().anyMatch(r -> r.getId() == 5L));
    }

    @Test
    void findAvailableRoomsByOptionalClassType_ShouldExcludeBookedRooms() {
        List<Room> results = roomRepository.findAvailableRoomsByOptionalClassType(
                null, // No classTypeId filter
                LocalDate.of(2024, 12, 1),
                LocalTime.of(10, 0) // Time when Room 1 is booked for Yoga
        );

        assertNotNull(results);
        assertEquals(4, results.size(), "Expected 4 rooms to be available (excluding Room 1)");
        assertFalse(results.stream().anyMatch(r -> r.getId() == 1L));
        assertTrue(results.stream().anyMatch(r -> r.getId() == 2L));
        assertTrue(results.stream().anyMatch(r -> r.getId() == 3L));
        assertTrue(results.stream().anyMatch(r -> r.getId() == 4L));
        assertTrue(results.stream().anyMatch(r -> r.getId() == 5L));
    }

    @Test
    void findAvailableRoomsByOptionalClassType_ShouldReturnEmptyWhenAllBooked() {
        List<Room> results = roomRepository.findAvailableRoomsByOptionalClassType(
                null, // No classTypeId filter
                LocalDate.of(2024, 12, 1),
                LocalTime.of(15, 0) // Time when Room 1 and Room 4 are booked
        );

        assertNotNull(results);
        assertEquals(3, results.size(), "Expected only unbooked rooms to be available (excluding Rooms 1 and 4)");
        assertFalse(results.stream().anyMatch(r -> r.getId() == 1L));
        assertFalse(results.stream().anyMatch(r -> r.getId() == 4L));
        assertTrue(results.stream().anyMatch(r -> r.getId() == 2L));
        assertTrue(results.stream().anyMatch(r -> r.getId() == 3L));
        assertTrue(results.stream().anyMatch(r -> r.getId() == 5L));
    }

    @Test
    void findAllByClassTypeIds_ShouldReturnRoomsForMatchingClassTypeIds() {
        // Test for Yoga (id = 1) and Pilates (id = 2)
        List<Long> classTypeIds = List.of(1L, 2L);

        List<Room> results = roomRepository.findAllByClassTypeIds(classTypeIds);

        assertNotNull(results);
        assertEquals(3, results.size(), "Expected 3 rooms that can host Yoga or Pilates");
        assertTrue(results.stream().anyMatch(r -> r.getId() == 1L), "Room 1 should be included for Yoga and Pilates");
        assertTrue(results.stream().anyMatch(r -> r.getId() == 3L), "Room 3 should be included for Yoga");
        assertTrue(results.stream().anyMatch(r -> r.getId() == 5L), "Room 5 should be included for Yoga");
    }
}
