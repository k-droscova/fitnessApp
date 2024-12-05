//
//  ClassType.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 04.12.2024.
//

import Foundation

struct ClassType: Codable {
    let classTypeId: Int?
    let name: String
    let instructors: [Int]
    let rooms: [Int]
    let fitnessClasses: [Int]
    
    enum CodingKeys: String, CodingKey {
        case classTypeId = "id"
        case name
        case instructors = "instructorIds"
        case rooms = "roomIds"
        case fitnessClasses = "fitnessClassIds"
    }
    
    init(
        classTypeId: Int? = nil,
        name: String,
        instructors: [Int] = [],
        rooms: [Int] = [],
        fitnessClasses: [Int] = []
    ) {
        self.classTypeId = classTypeId
        self.name = name
        self.instructors = instructors
        self.rooms = rooms
        self.fitnessClasses = fitnessClasses
    }
    
    init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        classTypeId = try container.decodeIfPresent(Int.self, forKey: .classTypeId)
        name = try container.decode(String.self, forKey: .name)
        instructors = try container.decode([Int].self, forKey: .instructors)
        rooms = try container.decode([Int].self, forKey: .rooms)
        fitnessClasses = try container.decode([Int].self, forKey: .fitnessClasses)
    }
}

extension ClassType: Identifiable, Equatable {
    var id: String { classTypeId.map(String.init) ?? UUID().uuidString }
}

extension ClassType {
    static let mock: ClassType = .init(
        classTypeId: 1,
        name: "Yoga",
        instructors: [1, 2],
        rooms: [101, 102],
        fitnessClasses: [201, 202]
    )
    
    static let mock2: ClassType = .init(
        classTypeId: 100,
        name: "Insanely long name over several lines",
        instructors: [1, 2],
        rooms: [101, 102, 000, 7363],
        fitnessClasses: [201, 202]
    )
}
