package cz.cvut.fit.tjv.fitnessApp.integration.repository;

import cz.cvut.fit.tjv.fitnessApp.domain.ClassType;
import cz.cvut.fit.tjv.fitnessApp.repository.ClassTypeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@AutoConfigureTestEntityManager
public class ClassTypeRepositoryIT {
    @Autowired
    private ClassTypeRepository classTypeRepository;

    @BeforeEach
    void setUp() {
        ClassType yoga = new ClassType();
        yoga.setName("Yoga");
        classTypeRepository.save(yoga);

        ClassType zumba = new ClassType();
        zumba.setName("Zumba");
        classTypeRepository.save(zumba);

        ClassType powerYoga = new ClassType();
        powerYoga.setName("Power Yoga");
        classTypeRepository.save(powerYoga);

        ClassType pilates = new ClassType();
        pilates.setName("Pilates");
        classTypeRepository.save(pilates);
    }

    @AfterEach
    void tearDown() {
        classTypeRepository.deleteAll();
    }

    @Test
    void findByNameContainingIgnoreCase_ReturnsMatchingResults_StartName() {
        Iterable<ClassType> results = classTypeRepository.findByNameContainingIgnoreCase("yo");

        List<ClassType> resultList = StreamSupport.stream(results.spliterator(), false).toList();

        assertNotNull(resultList);
        assertEquals(2, resultList.size());
        assertTrue(resultList.stream().anyMatch(ct -> "Yoga".equals(ct.getName())));
        assertTrue(resultList.stream().anyMatch(ct -> "Power Yoga".equals(ct.getName())));
    }

    @Test
    void findByNameContainingIgnoreCase_ReturnsMatchingResults_MiddleName() {
        Iterable<ClassType> results = classTypeRepository.findByNameContainingIgnoreCase("wer");

        List<ClassType> resultList = StreamSupport.stream(results.spliterator(), false).toList();

        assertNotNull(resultList);
        assertEquals(1, resultList.size());
        assertTrue(resultList.stream().anyMatch(ct -> "Power Yoga".equals(ct.getName())));
    }

    @Test
    void findByNameContainingIgnoreCase_ReturnsMatchingResults_EndName() {
        Iterable<ClassType> results = classTypeRepository.findByNameContainingIgnoreCase("tes");

        List<ClassType> resultList = StreamSupport.stream(results.spliterator(), false).toList();

        assertNotNull(resultList);
        assertEquals(1, resultList.size());
        assertTrue(resultList.stream().anyMatch(ct -> "Pilates".equals(ct.getName())));
    }

    @Test
    void findByNameContainingIgnoreCase_ReturnsMatchingResults_IgnoreCase() {
        Iterable<ClassType> results = classTypeRepository.findByNameContainingIgnoreCase("YOGA");

        List<ClassType> resultList = StreamSupport.stream(results.spliterator(), false).toList();

        assertNotNull(resultList);
        assertEquals(2, resultList.size());
        assertTrue(resultList.stream().anyMatch(ct -> "Yoga".equals(ct.getName())));
        assertTrue(resultList.stream().anyMatch(ct -> "Power Yoga".equals(ct.getName())));
    }

    @Test
    void findByNameContainingIgnoreCase_ReturnsEmptySet_WhenNoMatches() {
        Iterable<ClassType> results = classTypeRepository.findByNameContainingIgnoreCase("Dance");

        List<ClassType> resultList = StreamSupport.stream(results.spliterator(), false).toList();

        assertNotNull(resultList);
        assertTrue(resultList.isEmpty());
    }
}
