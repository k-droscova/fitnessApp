package cz.cvut.fit.tjv.fitnessApp.respository.file;

import cz.cvut.fit.tjv.fitnessApp.domain.Identifiable;
import cz.cvut.fit.tjv.fitnessApp.respository.CrudRepository;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public abstract class FileRepository<T extends Identifiable<ID>, ID> implements CrudRepository<T, ID> {
    private final Path storage;
    private final Class<T> entityType;

    protected FileRepository(Class<T> entityType, Path storage) {
        this.entityType = entityType;
        this.storage = storage;
    }

    @Override
    public T save(T entity) {
        try {
            var data = loadData();
            data.removeIf(e -> e.getId().equals(entity.getId()));
            data.add(entity);
            saveData(data);
            return entity;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Error when working with file storage", e);
        }
    }

    @Override
    public boolean existsById(ID id) {
        return findById(id).isPresent();
    }

    @Override
    public Optional<T> findById(ID id) {
        return findAll().stream().filter(i -> i.getId().equals(id)).findAny();
    }

    @Override
    public Collection<T> findAll() {
        try {
            return loadData();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Error when working with file storage", e);
        }
    }

    @Override
    public void deleteById(ID id) {
        try {
            var data = loadData();
            if (data.removeIf(i -> i.getId().equals(id))) {
                saveData(data);
            } else {
                System.err.println("Entity with ID " + id + " not found in storage.");
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Error deleting entity from file storage", e);
        }
    }

    private Set<T> loadData() throws IOException, ClassNotFoundException {
        return loadAllData().stream()
                .filter(entity -> entityType.isAssignableFrom(entity.getClass()))
                .map(entity -> (T) entity)
                .collect(Collectors.toSet());
    }

    private Collection<?> loadAllData() throws IOException, ClassNotFoundException {
        try (ObjectInputStream inStream = new ObjectInputStream(Files.newInputStream(storage))) {
            Object readData = inStream.readObject();
            if (readData instanceof Collection<?>) {
                return (Collection<?>) readData;
            } else {
                throw new ClassCastException("Stored data is not a Collection");
            }
        } catch (NoSuchFileException | EOFException e) {
            System.err.println("No data found, returning empty collection.");
            return new HashSet<>();
        }
    }

    private void saveData(Collection<T> data) throws IOException {
        Set<Object> loadedData;
        try {
            loadedData = new HashSet<>(loadAllData());
        } catch (ClassNotFoundException e) {
            System.err.println("Data in storage was invalid. Overwriting with new data.");
            loadedData = new HashSet<>();
        }

        loadedData.addAll(data);

        try (ObjectOutputStream outStream = new ObjectOutputStream(Files.newOutputStream(storage))) {
            outStream.writeObject(loadedData);
        }
    }
}