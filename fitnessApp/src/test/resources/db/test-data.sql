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
INSERT INTO class_type (id_class_type, name) VALUES (5, 'Spin');

-- Insert data into Room
INSERT INTO room (id_room, max_capacity) VALUES (1, 20); -- Standard room
INSERT INTO room (id_room, max_capacity) VALUES (2, 15); -- Smaller room
INSERT INTO room (id_room, max_capacity) VALUES (3, 10); -- Smallest room
INSERT INTO room (id_room, max_capacity) VALUES (4, 25); -- Large room
INSERT INTO room (id_room, max_capacity) VALUES (5, 30); -- Largest room

-- Insert data into Instructor
INSERT INTO instructor (id_employee, name, surname, birth_date)
VALUES (1, 'John', 'Doe', '1980-01-15');
INSERT INTO instructor (id_employee, name, surname, birth_date)
VALUES (2, 'Jane', 'Smith', '1990-05-20');
INSERT INTO instructor (id_employee, name, surname, birth_date)
VALUES (3, 'Alice', 'Johnson', '1985-07-12');
INSERT INTO instructor (id_employee, name, surname, birth_date)
VALUES (4, 'Bob', 'Brown', '1975-03-10');

-- Insert data into Room-ClassType relationships
INSERT INTO room_possible_class_types (id_room, id_class_type) VALUES (1, 1); -- Room 1 can host Yoga
INSERT INTO room_possible_class_types (id_room, id_class_type) VALUES (1, 2); -- Room 1 can host Pilates
INSERT INTO room_possible_class_types (id_room, id_class_type) VALUES (2, 3); -- Room 2 can host Zumba
INSERT INTO room_possible_class_types (id_room, id_class_type) VALUES (3, 1); -- Room 3 can host Yoga
INSERT INTO room_possible_class_types (id_room, id_class_type) VALUES (4, 4); -- Room 4 can host Power Yoga
INSERT INTO room_possible_class_types (id_room, id_class_type) VALUES (4, 5); -- Room 4 can host Spin
INSERT INTO room_possible_class_types (id_room, id_class_type) VALUES (5, 1); -- Room 5 can host Yoga
INSERT INTO room_possible_class_types (id_room, id_class_type) VALUES (5, 3); -- Room 5 can host Zumba

-- Insert data into Instructor-ClassType relationships
INSERT INTO instructor_specializations (id_employee, id_class_type) VALUES (1, 1); -- John specializes in Yoga
INSERT INTO instructor_specializations (id_employee, id_class_type) VALUES (2, 2); -- Jane specializes in Pilates
INSERT INTO instructor_specializations (id_employee, id_class_type) VALUES (2, 3); -- Jane specializes in Zumba
INSERT INTO instructor_specializations (id_employee, id_class_type) VALUES (3, 4); -- Alice specializes in Power Yoga
INSERT INTO instructor_specializations (id_employee, id_class_type) VALUES (4, 5); -- Bob specializes in Spin
INSERT INTO instructor_specializations (id_employee, id_class_type) VALUES (4, 1); -- Bob also specializes in Yoga

-- Insert data into FitnessClass
INSERT INTO fitness_class (id_class, date, time, capacity, id_employee, id_room, id_class_type)
VALUES (1, '2024-12-01', '10:00:00', 10, 1, 1, 1); -- Yoga class in Room 1
INSERT INTO fitness_class (id_class, date, time, capacity, id_employee, id_room, id_class_type)
VALUES (2, '2024-12-01', '15:00:00', 12, 2, 1, 2); -- Pilates class in Room 1
INSERT INTO fitness_class (id_class, date, time, capacity, id_employee, id_room, id_class_type)
VALUES (3, '2024-12-02', '10:00:00', 8, 2, 2, 3); -- Zumba class in Room 2
INSERT INTO fitness_class (id_class, date, time, capacity, id_employee, id_room, id_class_type)
VALUES (4, '2024-12-01', '15:00:00', 20, 3, 4, 4); -- Power Yoga in Room 4
INSERT INTO fitness_class (id_class, date, time, capacity, id_employee, id_room, id_class_type)
VALUES (5, '2024-12-02', '17:00:00', 15, 4, 5, 5); -- Spin class in Room 5

-- Insert data into Trainee
INSERT INTO trainee (id_trainee, email, name, surname)
VALUES (1, 'alice@example.com', 'Alice', 'Brown');
INSERT INTO trainee (id_trainee, email, name, surname)
VALUES (2, 'bob@example.com', 'Bob', 'White');
INSERT INTO trainee (id_trainee, email, name, surname)
VALUES (3, 'carol@example.com', 'Carol', 'Davis');
INSERT INTO trainee (id_trainee, email, name, surname)
VALUES (4, 'dave@example.com', 'Dave', 'Smith');

-- Insert data into FitnessClass-Trainee relationships
INSERT INTO attended_trainees (id_class, id_trainee) VALUES (1, 1); -- Alice attends Yoga
INSERT INTO attended_trainees (id_class, id_trainee) VALUES (1, 2); -- Bob attends Yoga
INSERT INTO attended_trainees (id_class, id_trainee) VALUES (1, 3); -- Carol attends Yoga

INSERT INTO attended_trainees (id_class, id_trainee) VALUES (2, 3); -- Carol attends Pilates
INSERT INTO attended_trainees (id_class, id_trainee) VALUES (2, 4); -- Dave attends Pilates

INSERT INTO attended_trainees (id_class, id_trainee) VALUES (3, 1); -- Alice attends Zumba
INSERT INTO attended_trainees (id_class, id_trainee) VALUES (3, 2); -- Bob attends Zumba
INSERT INTO attended_trainees (id_class, id_trainee) VALUES (3, 4); -- Dave attends Zumba

INSERT INTO attended_trainees (id_class, id_trainee) VALUES (4, 4); -- Dave attends Power Yoga
INSERT INTO attended_trainees (id_class, id_trainee) VALUES (4, 1); -- Alice attends Power Yoga
INSERT INTO attended_trainees (id_class, id_trainee) VALUES (4, 3); -- Carol attends Power Yoga
INSERT INTO attended_trainees (id_class, id_trainee) VALUES (4, 2); -- Bob attends Power Yoga

INSERT INTO attended_trainees (id_class, id_trainee) VALUES (5, 1); -- Alice attends Spin
INSERT INTO attended_trainees (id_class, id_trainee) VALUES (5, 3); -- Carol attends Spin