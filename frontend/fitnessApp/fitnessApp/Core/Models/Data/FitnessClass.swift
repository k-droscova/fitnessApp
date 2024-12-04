//
//  FitnessClass.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 04.12.2024.
//

import Foundation

struct FitnessClass: Codable {
    let id: Int?
    let capacity: Int
    let dateTime: Date
    let instructor: Int
    let room: Int
    let classType: Int
    let trainees: [Int]
    
    enum CodingKeys: String, CodingKey {
        case id
        case capacity
        case date
        case time
        case instructor = "instructorId"
        case room = "roomId"
        case classType = "classTypeId"
        case trainees = "traineeIds"
    }
    
    init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        id = try container.decodeIfPresent(Int.self, forKey: .id)
        capacity = try container.decode(Int.self, forKey: .capacity)
        instructor = try container.decode(Int.self, forKey: .instructor)
        room = try container.decode(Int.self, forKey: .room)
        classType = try container.decode(Int.self, forKey: .classType)
        trainees = try container.decode([Int].self, forKey: .trainees)
        
        // Combine `date` and `time` into a single `Date`
        let dateString = try container.decode(String.self, forKey: .date)
        let timeString = try container.decode(String.self, forKey: .time)
        guard let combinedDate = Date.Backend.combine(dateString: dateString, timeString: timeString) else {
            throw BaseError(
                context: .api,
                message: "Invalid date or time",
                code: .networking(.apiDecoding),
                logger: appDependencies.logger
            )
        }
        dateTime = combinedDate
    }
    
    func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)
        try container.encodeIfPresent(id, forKey: .id)
        try container.encode(capacity, forKey: .capacity)
        try container.encode(instructor, forKey: .instructor)
        try container.encode(room, forKey: .room)
        try container.encode(classType, forKey: .classType)
        try container.encode(trainees, forKey: .trainees)
        
        // Split `dateTime` into `date` and `time`
        let (date, time) = Date.Backend.split(dateTime: dateTime)
        try container.encode(date, forKey: .date)
        try container.encode(time, forKey: .time)
    }
}
