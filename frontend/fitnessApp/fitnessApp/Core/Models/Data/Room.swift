//
//  Room.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 04.12.2024.
//

import Foundation

struct Room: Codable {
    let roomId: Int?
    let maxCapacity: Int
    let classes: [Int]
    let classTypes: [Int]
    
    enum CodingKeys: String, CodingKey {
        case roomId = "id"
        case maxCapacity
        case classes = "fitnessClassIds"
        case classTypes = "classTypeIds"
    }
    
    init(
        roomId: Int? = nil,
        maxCapacity: Int,
        classes: [Int],
        classTypes: [Int]
    ) {
        self.roomId = roomId
        self.maxCapacity = maxCapacity
        self.classes = classes
        self.classTypes = classTypes
    }
    
    init(from decoder: any Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        self.roomId = try container.decodeIfPresent(Int.self, forKey: .roomId)
        self.maxCapacity = try container.decode(Int.self, forKey: .maxCapacity)
        self.classes = try container.decode([Int].self, forKey: .classes)
        self.classTypes = try container.decode([Int].self, forKey: .classTypes)
    }
    
    func encode(to encoder: any Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)
        try container.encodeIfPresent(self.roomId, forKey: .roomId)
        try container.encode(self.maxCapacity, forKey: .maxCapacity)
        try container.encode(self.classes, forKey: .classes)
        try container.encode(self.classTypes, forKey: .classTypes)
    }
}

extension Room: Identifiable, Equatable {
    var id: String { roomId.map(String.init) ?? UUID().uuidString }
}

extension Room {
    static let mock: Room = .init(
        roomId: 1,
        maxCapacity: 50,
        classes: [301, 302],
        classTypes: [401, 402]
    )
    
    static let mock2: Room = .init(
        roomId: 2,
        maxCapacity: 30,
        classes: [303, 304],
        classTypes: [403, 404]
    )
}
