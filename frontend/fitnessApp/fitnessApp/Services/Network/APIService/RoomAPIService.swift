//
//  RoomAPIService.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 04.12.2024.
//

import Foundation

protocol HasRoomAPIService {
    var roomAPIService: RoomAPIServicing { get }
}

protocol RoomAPIServicing {
    func allRooms() async throws -> [Room]
    func room(_ id: Int) async throws -> Room
    func postNewRoom(_ room: Room) async throws
    func updateRoom(_ id: Int, _ newRoom: Room) async throws
    func deleteRoom(_ id: Int) async throws
    func findAvailableRooms(classTypeId: Int?, date: String, time: String) async throws -> [Int]
}

final class RoomAPIService: BaseClass, RoomAPIServicing {
    typealias Dependencies = HasNetwork & HasLoggerService
    
    private let network: Networking
    private let logger: LoggerServicing
    
    init(dependencies: Dependencies) {
        self.logger = dependencies.logger
        self.network = dependencies.network
    }
    
    func allRooms() async throws -> [Room] {
        return try await network.performAuthorizedRequest(
            endpoint: Endpoint.room(.getAll),
            errorObject: APIResponseError.self
        )
    }
    
    func room(_ id: Int) async throws -> Room {
        return try await network.performAuthorizedRequest(
            endpoint: Endpoint.room(.getById(id: id)),
            errorObject: APIResponseError.self
        )
    }
    
    func postNewRoom(_ room: Room) async throws {
        let data = try JSONEncoder().encode(room)
        _ = try await network.performAuthorizedRequest(
            endpoint: Endpoint.room(.create),
            method: .POST,
            body: data,
            errorObject: APIResponseError.self
        ) as EmptyResponse
    }
    
    func updateRoom(_ id: Int, _ newRoom: Room) async throws {
        let data = try JSONEncoder().encode(newRoom)
        _ = try await network.performAuthorizedRequest(
            endpoint: Endpoint.room(.update(id: id)),
            method: .PUT,
            body: data,
            errorObject: APIResponseError.self
        ) as EmptyResponse
    }
    
    func deleteRoom(_ id: Int) async throws {
        _ = try await network.performAuthorizedRequest(
            endpoint: Endpoint.room(.delete(id: id)),
            method: .DELETE,
            errorObject: APIResponseError.self
        ) as EmptyResponse
    }
    
    func findAvailableRooms(classTypeId: Int?, date: String, time: String) async throws -> [Int] {
        return try await network.performAuthorizedRequest(
            endpoint: Endpoint.room(.getAvailable(classTypeId: classTypeId, date: date, time: time)),
            errorObject: APIResponseError.self
        )
    }
}
