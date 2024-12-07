//
//  TraineeAPIService.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 04.12.2024.
//

import Foundation

protocol HasTraineeAPIService {
    var traineeAPIService: TraineeAPIServicing { get }
}

protocol TraineeAPIServicing {
    func getAllTrainees() async throws -> [Trainee]
    func getTraineeById(_ id: Int) async throws -> Trainee
    func createTrainee(_ trainee: Trainee) async throws
    func updateTrainee(_ id: Int, with newTrainee: Trainee) async throws
    func deleteTrainee(_ id: Int) async throws
    func getTraineesByFitnessClassId(_ fitnessClassId: Int) async throws -> [Trainee]
    func searchByName(_ name: String) async throws -> [Trainee]
}

final class TraineeAPIService: BaseClass, TraineeAPIServicing {
    typealias Dependencies = HasNetwork & HasLoggerService
    
    private let network: Networking
    private let logger: LoggerServicing
    
    init(dependencies: Dependencies) {
        self.network = dependencies.network
        self.logger = dependencies.logger
    }
    
    // MARK: - Public Interface
    
    func getAllTrainees() async throws -> [Trainee] {
        return try await network.performAuthorizedRequest(
            endpoint: Endpoint.trainee(.getAll),
            errorObject: APIResponseError.self
        )
    }
    
    func getTraineeById(_ id: Int) async throws -> Trainee {
        return try await network.performAuthorizedRequest(
            endpoint: Endpoint.trainee(.getById(id: id)),
            errorObject: APIResponseError.self
        )
    }
    
    func createTrainee(_ trainee: Trainee) async throws {
        let data = try JSONEncoder().encode(trainee)
        _ = try await network.performAuthorizedRequest(
            endpoint: Endpoint.trainee(.create),
            method: .POST,
            body: data,
            errorObject: APIResponseError.self
        ) as EmptyResponse
    }
    
    func updateTrainee(_ id: Int, with newTrainee: Trainee) async throws {
        let data = try JSONEncoder().encode(newTrainee)
        _ = try await network.performAuthorizedRequest(
            endpoint: Endpoint.trainee(.update(id: id)),
            method: .PUT,
            body: data,
            errorObject: APIResponseError.self
        ) as EmptyResponse
    }
    
    func deleteTrainee(_ id: Int) async throws {
        _ = try await network.performAuthorizedRequest(
            endpoint: Endpoint.trainee(.delete(id: id)),
            method: .DELETE,
            errorObject: APIResponseError.self
        ) as EmptyResponse
    }
    
    func getTraineesByFitnessClassId(_ fitnessClassId: Int) async throws -> [Trainee] {
        return try await network.performAuthorizedRequest(
            endpoint: Endpoint.trainee(.getByFitnessClassId(fitnessClassId: fitnessClassId)),
            errorObject: APIResponseError.self
        )
    }
    
    func searchByName(_ name: String) async throws -> [Trainee] {
        return try await network.performAuthorizedRequest(
            endpoint: Endpoint.trainee(.search(input: name)),
            errorObject: APIResponseError.self
        )
    }
}
