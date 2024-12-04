//
//  Room.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 04.12.2024.
//

import Foundation

struct Room: Codable {
    let id: Int?
    let maxCapacity: Int
    let classes: [Int]
    let classTypes: [Int]
    
    enum CodingKeys: String, CodingKey {
        case id
        case maxCapacity
        case classes = "fitnessClassIds"
        case classTypes = "classTypeIds"
    }
    
    init(from decoder: any Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        self.id = try container.decodeIfPresent(Int.self, forKey: .id)
        self.maxCapacity = try container.decode(Int.self, forKey: .maxCapacity)
        self.classes = try container.decode([Int].self, forKey: .classes)
        self.classTypes = try container.decode([Int].self, forKey: .classTypes)
    }
    
    func encode(to encoder: any Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)
        try container.encodeIfPresent(self.id, forKey: .id)
        try container.encode(self.maxCapacity, forKey: .maxCapacity)
        try container.encode(self.classes, forKey: .classes)
        try container.encode(self.classTypes, forKey: .classTypes)
    }
}
