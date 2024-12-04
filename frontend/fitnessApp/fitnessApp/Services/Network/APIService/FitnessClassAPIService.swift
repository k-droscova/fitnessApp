//
//  FitnessClassAPIService.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 04.12.2024.
//

import Foundation

protocol HasFitnessClassAPIService {
    var fitnessClassAPIService: FitnessClassAPIServicing { get }
}

protocol FitnessClassAPIServicing {
    func allFitnessClasses() async throws -> [FitnessClass]
    func fitnessClass(_ id: Int) async throws -> FitnessClass
    func fitnessClassesByFilter(date: String?, startTime: String?, endTime: String?, roomId: Int?) async throws -> [FitnessClass]
    func postNewFitnessClass(_ fitnessClass: FitnessClass) async throws
    func updateFitnessClass(_ id: Int, _ newFitnessClass: FitnessClass) async throws
    func deleteFitnessClass(_ id: Int) async throws
    func addTraineeToFitnessClass(_ id: Int, _ traineeId: Int) async throws
    func getTraineesForFitnessClass(_ id: Int) async throws -> [Int]
}

final class FitnessClassAPIService: FitnessClassAPIServicing {
    typealias Dependencies = HasNetwork & HasLoggerService
    
    private let network: Networking
    private let logger: LoggerServicing
    
    init(dependencies: Dependencies) {
        self.logger = dependencies.logger
        self.network = dependencies.network
    }
    
    // MARK: - Public Interface
    
    func allFitnessClasses() async throws -> [FitnessClass] {
        return try await network.performAuthorizedRequest(
            endpoint: Endpoint.fitnessClass(.getAll),
            errorObject: APIResponseError.self
        )
    }
    
    func fitnessClass(_ id: Int) async throws -> FitnessClass {
        return try await network.performAuthorizedRequest(
            endpoint: Endpoint.fitnessClass(.getById(id: id)),
            errorObject: APIResponseError.self
        )
    }
    
    func fitnessClassesByFilter(date: String?, startTime: String?, endTime: String?, roomId: Int?) async throws -> [FitnessClass] {
        let endpoint = Endpoint.fitnessClass(.getByFilter(
            date: date,
            startTime: startTime,
            endTime: endTime,
            roomId: roomId
        ))
        return try await network.performAuthorizedRequest(
            endpoint: endpoint,
            errorObject: APIResponseError.self
        )
    }
    
    func postNewFitnessClass(_ fitnessClass: FitnessClass) async throws {
        let data = try JSONEncoder().encode(fitnessClass)
        _ = try await network.performAuthorizedRequest(
            endpoint: Endpoint.fitnessClass(.create),
            method: .POST,
            body: data,
            errorObject: APIResponseError.self
        ) as EmptyResponse
    }
    
    func updateFitnessClass(_ id: Int, _ newFitnessClass: FitnessClass) async throws {
        let data = try JSONEncoder().encode(newFitnessClass)
        _ = try await network.performAuthorizedRequest(
            endpoint: Endpoint.fitnessClass(.update(id: id)),
            method: .PUT,
            body: data,
            errorObject: APIResponseError.self
        ) as EmptyResponse
    }
    
    func deleteFitnessClass(_ id: Int) async throws {
        _ = try await network.performAuthorizedRequest(
            endpoint: Endpoint.fitnessClass(.delete(id: id)),
            method: .DELETE,
            errorObject: APIResponseError.self
        ) as EmptyResponse
    }
    
    func addTraineeToFitnessClass(_ id: Int, _ traineeId: Int) async throws {
        _ = try await network.performAuthorizedRequest(
            endpoint: Endpoint.fitnessClass(.addTrainee(id: id, traineeId: traineeId)),
            method: .POST,
            errorObject: APIResponseError.self
        ) as EmptyResponse
    }
    
    func getTraineesForFitnessClass(_ id: Int) async throws -> [Int] {
        return try await network.performAuthorizedRequest(
            endpoint: Endpoint.fitnessClass(.getTrainees(id: id)),
            errorObject: APIResponseError.self
        )
    }
}
