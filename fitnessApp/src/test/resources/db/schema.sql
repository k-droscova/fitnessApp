-- Drop existing tables if they exist (optional for repeated test runs)
DROP TABLE IF EXISTS attended_trainees;
DROP TABLE IF EXISTS room_possible_class_types;
DROP TABLE IF EXISTS instructor_specializations;
DROP TABLE IF EXISTS fitness_class;
DROP TABLE IF EXISTS room;
DROP TABLE IF EXISTS trainee;
DROP TABLE IF EXISTS instructor;
DROP TABLE IF EXISTS class_type;

-- Create ClassType table
CREATE TABLE class_type (
                            id_class_type BIGINT AUTO_INCREMENT PRIMARY KEY,
                            name VARCHAR(255) NOT NULL UNIQUE
);

-- Create Room table
CREATE TABLE room (
                      id_room BIGINT AUTO_INCREMENT PRIMARY KEY,
                      max_capacity INT NOT NULL
);

-- Create Instructor table
CREATE TABLE instructor (
                            id_employee BIGINT AUTO_INCREMENT PRIMARY KEY,
                            name VARCHAR(255) NOT NULL,
                            surname VARCHAR(255) NOT NULL,
                            birth_date DATE NOT NULL
);

-- Create Trainee table
CREATE TABLE trainee (
                         id_trainee BIGINT AUTO_INCREMENT PRIMARY KEY,
                         email VARCHAR(255) NOT NULL UNIQUE,
                         name VARCHAR(255) NOT NULL,
                         surname VARCHAR(255) NOT NULL
);

-- Create FitnessClass table
CREATE TABLE fitness_class (
                               id_class BIGINT AUTO_INCREMENT PRIMARY KEY,
                               date DATE NOT NULL,
                               time TIME NOT NULL,
                               capacity INT NOT NULL,
                               id_employee BIGINT NOT NULL,
                               id_room BIGINT NOT NULL,
                               id_class_type BIGINT NOT NULL,
                               FOREIGN KEY (id_employee) REFERENCES instructor (id_employee) ON DELETE CASCADE,
                               FOREIGN KEY (id_room) REFERENCES room (id_room) ON DELETE CASCADE,
                               FOREIGN KEY (id_class_type) REFERENCES class_type (id_class_type) ON DELETE CASCADE
);

-- Create Room-ClassType many-to-many relationship table
CREATE TABLE room_possible_class_types (
                                           id_room BIGINT NOT NULL,
                                           id_class_type BIGINT NOT NULL,
                                           PRIMARY KEY (id_room, id_class_type),
                                           FOREIGN KEY (id_room) REFERENCES room (id_room) ON DELETE CASCADE,
                                           FOREIGN KEY (id_class_type) REFERENCES class_type (id_class_type) ON DELETE CASCADE
);

-- Create Instructor-ClassType many-to-many relationship table
CREATE TABLE instructor_specializations (
                                            id_employee BIGINT NOT NULL,
                                            id_class_type BIGINT NOT NULL,
                                            PRIMARY KEY (id_employee, id_class_type),
                                            FOREIGN KEY (id_employee) REFERENCES instructor (id_employee) ON DELETE CASCADE,
                                            FOREIGN KEY (id_class_type) REFERENCES class_type (id_class_type) ON DELETE CASCADE
);

-- Create FitnessClass-Trainee many-to-many relationship table
CREATE TABLE attended_trainees (
                                   id_class BIGINT NOT NULL,
                                   id_trainee BIGINT NOT NULL,
                                   PRIMARY KEY (id_class, id_trainee),
                                   FOREIGN KEY (id_class) REFERENCES fitness_class (id_class) ON DELETE CASCADE,
                                   FOREIGN KEY (id_trainee) REFERENCES trainee (id_trainee) ON DELETE CASCADE
);