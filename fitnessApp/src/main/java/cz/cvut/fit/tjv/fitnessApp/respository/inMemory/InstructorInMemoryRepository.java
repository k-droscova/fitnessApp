package cz.cvut.fit.tjv.fitnessApp.respository.inMemory;

import cz.cvut.fit.tjv.fitnessApp.domain.Instructor;
import cz.cvut.fit.tjv.fitnessApp.respository.InstructorRepository;

import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class InstructorInMemoryRepository extends InMemoryRepository<Instructor, Integer> implements InstructorRepository {
    @Override
    public Optional<Instructor> findByName(String name) {
        // Convert Iterable to Stream
        Stream<Instructor> instructorStream = StreamSupport.stream(findAll().spliterator(), false);
        return instructorStream
                .filter(i -> i.getName().equalsIgnoreCase(name))
                .findFirst();
    }
}