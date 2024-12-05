//
//  Untitled.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 04.12.2024.
//

import Foundation

struct Trainee: Codable {
    let traineeId: Int?
    let email: String
    let name: String
    let surname: String
    let classes: [Int]
    
    enum CodingKeys: String, CodingKey {
        case traineeId
        case email
        case name
        case surname
        case classes = "fitnessClassIds"
    }
    
    init(
        traineeId: Int? = nil,
        email: String,
        name: String,
        surname: String,
        classes: [Int]
    ) {
        self.traineeId = traineeId
        self.email = email
        self.name = name
        self.surname = surname
        self.classes = classes
    }
    
    init(from decoder: any Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        self.traineeId = try container.decodeIfPresent(Int.self, forKey: .traineeId)
        self.email = try container.decode(String.self, forKey: .email)
        self.name = try container.decode(String.self, forKey: .name)
        self.surname = try container.decode(String.self, forKey: .surname)
        self.classes = try container.decode([Int].self, forKey: .classes)
    }
    
    func encode(to encoder: any Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)
        try container.encodeIfPresent(self.traineeId, forKey: .traineeId)
        try container.encode(self.email, forKey: .email)
        try container.encode(self.name, forKey: .name)
        try container.encode(self.surname, forKey: .surname)
        try container.encode(self.classes, forKey: .classes)
    }
}

extension Trainee: Identifiable, Equatable {
    var id: String { traineeId.map(String.init) ?? UUID().uuidString }
}

extension Trainee {
    static let mock: Trainee = .init(
        traineeId: 1,
        email: "trainee1@example.com",
        name: "Alice",
        surname: "Johnson",
        classes: [501, 502]
    )
    
    static let mock2: Trainee = .init(
        traineeId: 2,
        email: "trainee2@example.com",
        name: "Bob",
        surname: "Williams",
        classes: [503, 504]
    )
}
