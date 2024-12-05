//
//  FitnessClass.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 04.12.2024.
//

import Foundation

struct FitnessClass: Codable {
    let fitnessClassId: Int?
    let capacity: Int
    let dateTime: Date
    let instructor: Int
    let room: Int
    let classType: Int
    let trainees: [Int]
    
    enum CodingKeys: String, CodingKey {
        case fitnessClassId = "id"
        case capacity
        case date
        case time
        case instructor = "instructorId"
        case room = "roomId"
        case classType = "classTypeId"
        case trainees = "traineeIds"
    }

    init(
        fitnessClassId: Int? = nil,
        capacity: Int,
        dateTime: Date,
        instructor: Int,
        room: Int,
        classType: Int,
        trainees: [Int] = []
    ) {
        self.fitnessClassId = fitnessClassId
        self.capacity = capacity
        self.dateTime = dateTime
        self.instructor = instructor
        self.room = room
        self.classType = classType
        self.trainees = trainees
    }

    init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        fitnessClassId = try container.decodeIfPresent(Int.self, forKey: .fitnessClassId)
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
        try container.encodeIfPresent(fitnessClassId, forKey: .fitnessClassId)
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

extension FitnessClass: Identifiable, Equatable {
    var id: String { fitnessClassId.map(String.init) ?? UUID().uuidString }
}

extension FitnessClass {
    static let mock: FitnessClass = .init(
        fitnessClassId: 1,
        capacity: 20,
        dateTime: Date(),
        instructor: 101,
        room: 201,
        classType: 301,
        trainees: [1, 2, 3]
    )
    
    static let mock2: FitnessClass = .init(
        fitnessClassId: 2,
        capacity: 15,
        dateTime: Date().addingTimeInterval(3600),
        instructor: 102,
        room: 202,
        classType: 302,
        trainees: [4, 5, 6]
    )
}
