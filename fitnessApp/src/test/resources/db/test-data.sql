-- Clear existing data
DELETE FROM attended_trainees;
DELETE FROM room_possible_class_types;
DELETE FROM instructor_specializations;
DELETE FROM fitness_class;
DELETE FROM room;
DELETE FROM trainee;
DELETE FROM instructor;
DELETE FROM class_type;

ALTER TABLE class_type ALTER COLUMN id_class_type RESTART WITH 1;
ALTER TABLE room ALTER COLUMN id_room RESTART WITH 1;
ALTER TABLE instructor ALTER COLUMN id_employee RESTART WITH 1;
ALTER TABLE trainee ALTER COLUMN id_trainee RESTART WITH 1;
ALTER TABLE fitness_class ALTER COLUMN id_class RESTART WITH 1;

-- Insert data into ClassType
INSERT INTO class_type (name) VALUES ('Yoga');
INSERT INTO class_type (name) VALUES ('Pilates');
INSERT INTO class_type (name) VALUES ('Zumba');
INSERT INTO class_type (name) VALUES ('Power Yoga');
INSERT INTO class_type (name) VALUES ('Spin');

-- Insert data into Room
INSERT INTO room (max_capacity) VALUES (20); -- Standard room
INSERT INTO room (max_capacity) VALUES (15); -- Smaller room
INSERT INTO room (max_capacity) VALUES (10); -- Smallest room
INSERT INTO room (max_capacity) VALUES (25); -- Large room
INSERT INTO room (max_capacity) VALUES (30); -- Largest room

-- Insert data into Instructor
INSERT INTO instructor (name, surname, birth_date)
VALUES ('John', 'Doe', '1980-01-15');
INSERT INTO instructor (name, surname, birth_date)
VALUES ('Jane', 'Smith', '1990-05-20');
INSERT INTO instructor (name, surname, birth_date)
VALUES ('Alice', 'Johnson', '1985-07-12');
INSERT INTO instructor (name, surname, birth_date)
VALUES ('Bob', 'Brown', '1975-03-10');

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
INSERT INTO fitness_class (date, time, capacity, id_employee, id_room, id_class_type)
VALUES ('2024-12-01', '10:00:00', 10, 1, 1, 1); -- Yoga class in Room 1
INSERT INTO fitness_class (date, time, capacity, id_employee, id_room, id_class_type)
VALUES ('2024-12-01', '15:00:00', 12, 2, 1, 2); -- Pilates class in Room 1
INSERT INTO fitness_class (date, time, capacity, id_employee, id_room, id_class_type)
VALUES ('2024-12-02', '10:00:00', 8, 2, 2, 3); -- Zumba class in Room 2
INSERT INTO fitness_class (date, time, capacity, id_employee, id_room, id_class_type)
VALUES ('2024-12-01', '15:00:00', 20, 3, 4, 4); -- Power Yoga in Room 4
INSERT INTO fitness_class (date, time, capacity, id_employee, id_room, id_class_type)
VALUES ('2024-12-02', '17:00:00', 15, 4, 5, 5); -- Spin class in Room 5

-- Insert data into Trainee
INSERT INTO trainee (email, name, surname)
VALUES ('alice@example.com', 'Alice', 'Brown');
INSERT INTO trainee (email, name, surname)
VALUES ('bob@example.com', 'Bob', 'White');
INSERT INTO trainee (email, name, surname)
VALUES ('carol@example.com', 'Carol', 'Davis');
INSERT INTO trainee (email, name, surname)
VALUES ('dave@example.com', 'Dave', 'Smith');

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