//
//  Instructor.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 04.12.2024.
//

import Foundation

struct Instructor: Codable {
    let id: Int?
    let name: String
    let surname: String
    let birthDate: Date
    let specilizations: [Int]
    let classes: [Int]
    
    enum CodingKeys: String, CodingKey {
        case id
        case name
        case surname
        case birthDate
        case specilizations = "classTypeIds"
        case classes = "fitnessClassIds"
    }
    
    init(from decoder: any Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        self.id = try container.decodeIfPresent(Int.self, forKey: .id)
        self.name = try container.decode(String.self, forKey: .name)
        self.surname = try container.decode(String.self, forKey: .surname)
        self.birthDate = try container.decode(Date.self, forKey: .birthDate)
        self.specilizations = try container.decode([Int].self, forKey: .specilizations)
        self.classes = try container.decode([Int].self, forKey: .classes)
    }
    
    func encode(to encoder: any Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)
        try container.encodeIfPresent(self.id, forKey: .id)
        try container.encode(self.name, forKey: .name)
        try container.encode(self.surname, forKey: .surname)
        try container.encode(self.birthDate, forKey: .birthDate)
        try container.encode(self.specilizations, forKey: .specilizations)
        try container.encode(self.classes, forKey: .classes)
    }
}
