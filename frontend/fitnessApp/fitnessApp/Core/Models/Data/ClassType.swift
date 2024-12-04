//
//  ClassType.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 04.12.2024.
//

import Foundation

struct ClassType: Codable {
    let id: Int?
    let name: String
    let instructors: [Int]
    let rooms: [Int]
    let fitnessClasses: [Int]
    
    enum CodingKeys: String, CodingKey {
        case id
        case name
        case instructors = "instructorIds"
        case rooms = "roomIds"
        case fitnessClasses = "fitnessClassIds"
    }
    
    init(
        id: Int? = nil,
        name: String,
        instructors: [Int] = [],
        rooms: [Int] = [],
        fitnessClasses: [Int] = []
    ) {
        self.id = id
        self.name = name
        self.instructors = instructors
        self.rooms = rooms
        self.fitnessClasses = fitnessClasses
    }
    
    init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        id = try container.decodeIfPresent(Int.self, forKey: .id)
        name = try container.decode(String.self, forKey: .name)
        instructors = try container.decode([Int].self, forKey: .instructors)
        rooms = try container.decode([Int].self, forKey: .rooms)
        fitnessClasses = try container.decode([Int].self, forKey: .fitnessClasses)
    }
}
