//
//  InstructorAPIService.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 04.12.2024.
//

import Foundation

protocol HasInstructorAPIService {
    var instructorAPIService: InstructorAPIServicing { get }
}

protocol InstructorAPIServicing {
    func allInstructors() async throws -> [Instructor]
    func instructor(_ id: Int) async throws -> Instructor
    func postNewInstructor(_ instructor: Instructor) async throws
    func updateInstructor(_ id: Int, _ newInstructor: Instructor) async throws
    func deleteInstructor(_ id: Int) async throws
    func findAvailableInstructors(classTypeId: Int?, date: String, time: String) async throws -> [Int]
    func searchInstructors(name: String?, surname: String?, input: String?) async throws -> [Instructor]
}

final class InstructorAPIService: BaseClass, InstructorAPIServicing {
    typealias Dependencies = HasNetwork & HasLoggerService
    
    private let network: Networking
    private let logger: LoggerServicing
    
    init(dependencies: Dependencies) {
        self.logger = dependencies.logger
        self.network = dependencies.network
    }
    
    // MARK: - Public Interface
    
    func allInstructors() async throws -> [Instructor] {
        return try await network.performAuthorizedRequest(
            endpoint: Endpoint.instructor(.getAll),
            errorObject: APIResponseError.self
        )
    }
    
    func instructor(_ id: Int) async throws -> Instructor {
        return try await network.performAuthorizedRequest(
            endpoint: Endpoint.instructor(.getById(id: id)),
            errorObject: APIResponseError.self
        )
    }
    
    func postNewInstructor(_ instructor: Instructor) async throws {
        let data = try JSONEncoder().encode(instructor)
        _ = try await network.performAuthorizedRequest(
            endpoint: Endpoint.instructor(.create),
            method: .POST,
            body: data,
            errorObject: APIResponseError.self
        ) as EmptyResponse
    }
    
    func updateInstructor(_ id: Int, _ newInstructor: Instructor) async throws {
        let data = try JSONEncoder().encode(newInstructor)
        _ = try await network.performAuthorizedRequest(
            endpoint: Endpoint.instructor(.update(id: id)),
            method: .PUT,
            body: data,
            errorObject: APIResponseError.self
        ) as EmptyResponse
    }
    
    func deleteInstructor(_ id: Int) async throws {
        _ = try await network.performAuthorizedRequest(
            endpoint: Endpoint.instructor(.delete(id: id)),
            method: .DELETE,
            errorObject: APIResponseError.self
        ) as EmptyResponse
    }
    
    func findAvailableInstructors(classTypeId: Int?, date: String, time: String) async throws -> [Int] {
        return try await network.performAuthorizedRequest(
            endpoint: Endpoint.instructor(.getAvailable(classTypeId: classTypeId, date: date, time: time)),
            errorObject: APIResponseError.self
        )
    }
    
    func searchInstructors(name: String?, surname: String?, input: String?) async throws -> [Instructor] {
        return try await network.performAuthorizedRequest(
            endpoint: Endpoint.instructor(.search(name: name, surname: surname, input: input)),
            errorObject: APIResponseError.self
        )
    }
}
