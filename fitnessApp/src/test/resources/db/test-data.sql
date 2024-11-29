-- Clear existing data
DELETE FROM attended_trainees;
DELETE FROM room_possible_class_types;
DELETE FROM instructor_specializations;
DELETE FROM fitness_class;
DELETE FROM room;
DELETE FROM trainee;
DELETE FROM instructor;
DELETE FROM class_type;

-- Insert data into ClassType
INSERT INTO class_type (id_class_type, name) VALUES (1, 'Yoga');
INSERT INTO class_type (id_class_type, name) VALUES (2, 'Pilates');
INSERT INTO class_type (id_class_type, name) VALUES (3, 'Zumba');
INSERT INTO class_type (id_class_type, name) VALUES (4, 'Power Yoga');

-- Insert data into Room
INSERT INTO room (id_room, max_capacity) VALUES (1, 20);
INSERT INTO room (id_room, max_capacity) VALUES (2, 15);

-- Insert data into Instructor
INSERT INTO instructor (id_employee, name, surname, birth_date)
VALUES (1, 'John', 'Doe', '1980-01-15');
INSERT INTO instructor (id_employee, name, surname, birth_date)
VALUES (2, 'Jane', 'Smith', '1990-05-20');

-- Insert data into Room-ClassType relationships (Many-to-Many)
INSERT INTO room_possible_class_types (id_room, id_class_type) VALUES (1, 1);
INSERT INTO room_possible_class_types (id_room, id_class_type) VALUES (1, 2);
INSERT INTO room_possible_class_types (id_room, id_class_type) VALUES (2, 3);

-- Insert data into Instructor-ClassType relationships (Many-to-Many)
INSERT INTO instructor_specializations (id_employee, id_class_type) VALUES (1, 1);
INSERT INTO instructor_specializations (id_employee, id_class_type) VALUES (2, 2);
INSERT INTO instructor_specializations (id_employee, id_class_type) VALUES (2, 3);

-- Insert data into FitnessClass
INSERT INTO fitness_class (id_class, date, time, capacity, id_employee, id_room, id_class_type)
VALUES (1, '2024-12-01', '10:00:00', 10, 1, 1, 1);
INSERT INTO fitness_class (id_class, date, time, capacity, id_employee, id_room, id_class_type)
VALUES (2, '2024-12-01', '15:00:00', 12, 1, 1, 2);
INSERT INTO fitness_class (id_class, date, time, capacity, id_employee, id_room, id_class_type)
VALUES (3, '2024-12-02', '10:00:00', 8, 2, 2, 3);

-- Insert data into Trainee
INSERT INTO trainee (id_trainee, email, name, surname)
VALUES (1, 'alice@example.com', 'Alice', 'Brown');
INSERT INTO trainee (id_trainee, email, name, surname)
VALUES (2, 'bob@example.com', 'Bob', 'White');
INSERT INTO trainee (id_trainee, email, name, surname)
VALUES (3, 'carol@example.com', 'Carol', 'Davis');

-- Insert data into FitnessClass-Trainee relationships (Many-to-Many)
INSERT INTO attended_trainees (id_class, id_trainee) VALUES (1, 1);
INSERT INTO attended_trainees (id_class, id_trainee) VALUES (1, 2);
INSERT INTO attended_trainees (id_class, id_trainee) VALUES (2, 3);
INSERT INTO attended_trainees (id_class, id_trainee) VALUES (3, 1);