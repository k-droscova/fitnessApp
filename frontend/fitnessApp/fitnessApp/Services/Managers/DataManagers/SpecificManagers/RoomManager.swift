//
//  RoomManager.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 04.12.2024.
//

import Foundation

protocol RoomManagerFlowDelegate: NSObject {
    func onRoomCreated()
    func onRoomUpdated()
    func onRoomDeleted()
    func onError(_ error: Error)
}

protocol HasRoomManager {
    var roomManager: any RoomManaging { get }
}

protocol RoomManaging {
    var delegate: RoomManagerFlowDelegate? { get set }
    var allRooms: [Room] { get }
    func fetchRooms() async
    func fetchRoomById(_ id: Int) async -> Room?
    func createRoom(_ room: Room) async
    func updateRoom(_ id: Int, _ newRoom: Room) async
    func deleteRoom(_ id: Int) async
    func findAvailableRooms(classTypeId: Int?, date: String, time: String) async -> [Int]
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
    
    // MARK: - Public Interface
    
    func fetchRooms() async {
        do {
            let result = try await roomAPIService.allRooms()
            allRooms = result
        } catch {
            delegate?.onError(error)
        }
    }
    
    func fetchRoomById(_ id: Int) async -> Room? {
        do {
            return try await roomAPIService.room(id)
        } catch {
            delegate?.onError(error)
            return nil
        }
    }
    
    func createRoom(_ room: Room) async {
        do {
            try await roomAPIService.postNewRoom(room)
            allRooms.append(room)
            delegate?.onRoomCreated()
        } catch {
            delegate?.onError(error)
        }
    }
    
    func updateRoom(_ id: Int, _ newRoom: Room) async {
        do {
            try await roomAPIService.updateRoom(id, newRoom)
            guard let index = allRooms.firstIndex(where: { $0.id == id }) else {
                throw BaseError(
                    context: .system,
                    message: "Room with id \(id) not found in local cache",
                    logger: self.logger
                )
            }
            allRooms[index] = newRoom
            delegate?.onRoomUpdated()
        } catch {
            delegate?.onError(error)
        }
    }
    
    func deleteRoom(_ id: Int) async {
        do {
            try await roomAPIService.deleteRoom(id)
            allRooms.removeAll { $0.id == id }
            delegate?.onRoomDeleted()
        } catch {
            delegate?.onError(error)
        }
    }
    
    func findAvailableRooms(classTypeId: Int?, date: String, time: String) async -> [Int] {
        do {
            return try await roomAPIService.findAvailableRooms(
                classTypeId: classTypeId,
                date: date,
                time: time
            )
        } catch {
            delegate?.onError(error)
            return []
        }
    }
}
