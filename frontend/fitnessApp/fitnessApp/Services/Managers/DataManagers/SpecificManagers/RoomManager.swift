//
//  RoomManager.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 04.12.2024.
//

import Foundation

protocol RoomManagerFlowDelegate: NSObject {}

protocol HasRoomManager {
    var roomManager: RoomManaging { get }
}

protocol RoomManaging {
    var delegate: RoomManagerFlowDelegate? { get set }
    var allRooms: [Room] { get }
    func fetchRooms() async throws
    func fetchRoomById(_ id: Int) async throws -> Room
    func createRoom(_ room: Room) async throws
    func updateRoom(_ id: Int, _ newRoom: Room) async throws
    func deleteRoom(_ id: Int) async throws
    func findAvailableRooms(classTypeId: Int?, date: String, time: String) async throws -> [Int]
}

final class RoomManager: BaseClass, RoomManaging {
    typealias Dependencies = HasLoggerService & HasRoomAPIService
    
    weak var delegate: RoomManagerFlowDelegate?
    private(set) var allRooms: [Room] = []
    
    private let logger: LoggerServicing
    private let roomAPIService: RoomAPIServicing
    
    init(dependencies: Dependencies) {
        self.logger = dependencies.logger
        self.roomAPIService = dependencies.roomAPIService
    }
    
    func fetchRooms() async throws {
        allRooms = try await roomAPIService.allRooms()
    }
    
    func fetchRoomById(_ id: Int) async throws -> Room {
        return try await roomAPIService.room(id)
    }
    
    func createRoom(_ room: Room) async throws {
        try await roomAPIService.postNewRoom(room)
        allRooms.append(room)
    }
    
    func updateRoom(_ id: Int, _ newRoom: Room) async throws {
        try await roomAPIService.updateRoom(id, newRoom)
        guard let index = allRooms.firstIndex(where: { $0.roomId == id }) else {
            throw BaseError(
                context: .system,
                message: "Room with id \(id) not found in local cache",
                logger: logger
            )
        }
        allRooms[index] = newRoom
    }
    
    func deleteRoom(_ id: Int) async throws {
        try await roomAPIService.deleteRoom(id)
        allRooms.removeAll { $0.roomId == id }
    }
    
    func findAvailableRooms(classTypeId: Int?, date: String, time: String) async throws -> [Int] {
        return try await roomAPIService.findAvailableRooms(classTypeId: classTypeId, date: date, time: time)
    }
}
