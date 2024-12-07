//
//  Instructor.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 04.12.2024.
//

import Foundation

struct Instructor: Codable, Hashable {
    let instructorId: Int?
    let name: String
    let surname: String
    let birthDate: Date
    let specializations: [Int]
    let classes: [Int]
    
    var age: Int { Calendar.current.calculateAge(from: birthDate) ?? 0 }
    
    enum CodingKeys: String, CodingKey {
        case instructorId = "id"
        case name
        case surname
        case birthDate
        case specializations = "classTypeIds"
        case classes = "fitnessClassIds"
    }
    
    init(
        instructorId: Int? = nil,
        name: String,
        surname: String,
        birthDate: Date,
        specializations: [Int] = [],
        classes: [Int] = []
    ) {
        self.instructorId = instructorId
        self.name = name
        self.surname = surname
        self.birthDate = birthDate
        self.specializations = specializations
        self.classes = classes
    }
    
    init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        instructorId = try container.decodeIfPresent(Int.self, forKey: .instructorId)
        name = try container.decode(String.self, forKey: .name)
        surname = try container.decode(String.self, forKey: .surname)
        specializations = try container.decode([Int].self, forKey: .specializations)
        classes = try container.decode([Int].self, forKey: .classes)
        
        // Decode `birthDate` from LocalDate (String)
        let birthDateString = try container.decode(String.self, forKey: .birthDate)
        guard let parsedDate = Date.Backend.fromLocalDateString(birthDateString) else {
            throw BaseError(
                context: .api,
                message: "Invalid birthDate format",
                code: .networking(.apiDecoding),
                logger: appDependencies.logger
            )
        }
        birthDate = parsedDate
    }
    
    func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)
        try container.encodeIfPresent(instructorId, forKey: .instructorId)
        try container.encode(name, forKey: .name)
        try container.encode(surname, forKey: .surname)
        try container.encode(specializations, forKey: .specializations)
        try container.encode(classes, forKey: .classes)
        
        // Encode `birthDate` to LocalDate (String)
        let birthDateString = Date.Backend.toLocalDateString(birthDate)
        try container.encode(birthDateString, forKey: .birthDate)
    }
}

extension Instructor: Identifiable, Equatable {
    var id: String { instructorId.map(String.init) ?? UUID().uuidString }
}

extension Instructor {
    static let mock: Instructor = .init(
        instructorId: 1,
        name: "John",
        surname: "Doe",
        birthDate: Date(timeIntervalSince1970: 315532800), // Example date: 01/01/1980
        specializations: [101, 102],
        classes: [201, 202]
    )
    
    static let mock2: Instructor = .init(
        instructorId: 2,
        name: "Jane",
        surname: "Smith",
        birthDate: Date(timeIntervalSince1970: 504921600), // Example date: 01/01/1986
        specializations: [103, 104],
        classes: [203, 204]
    )
}

extension Instructor: CustomStringConvertible {
    var description: String { "\(name) \(surname)" }
}
