//
//  ClassTypeAPIService.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 04.12.2024.
//

import Foundation

protocol HasClassTypeAPIService {
    var classTypeAPIService: ClassTypeAPIServicing { get }
}

protocol ClassTypeAPIServicing {
    func allClassTypes() async throws -> [ClassType]
    func classType(_ id: Int) async throws -> ClassType
    func classTypeByName(_ name: String) async throws -> [ClassType]
    func postNewClassType(_ classType: ClassType) async throws -> ClassType
    func updateClassType(_ id: Int, _ newClassType: ClassType) async throws
    func deleteClassType(_ id: Int) async throws
    func instructorsForClassType(_ id: Int) async throws -> [Int]
    func roomsForClassType(_ id: Int) async throws -> [Int]
    func fitnessClassesForClassType(_ id: Int) async throws -> [Int]
}

final class ClassTypeAPIService: BaseClass, ClassTypeAPIServicing {
    typealias Dependencies = HasNetwork & HasLoggerService
    
    private let network: Networking
    private let logger: LoggerServicing
    
    init(dependencies: Dependencies) {
        self.logger = dependencies.logger
        self.network = dependencies.network
    }
    
    // MARK: - Public Interface
    
    func allClassTypes() async throws -> [ClassType] {
        return try await network.performAuthorizedRequest(
            endpoint: Endpoint.classType(.get),
            errorObject: APIResponseError.self
        )
    }
    
    func classType(_ id: Int) async throws -> ClassType {
        return try await network.performAuthorizedRequest(
            endpoint: Endpoint.classType(.getById(id: id)),
            errorObject: APIResponseError.self
        )
    }
    
    func classTypeByName(_ name: String) async throws -> [ClassType] {
        return try await network.performAuthorizedRequest(
            endpoint: Endpoint.classType(.getByName(name: name)),
            errorObject: APIResponseError.self
        )
    }
    
    func postNewClassType(_ classType: ClassType) async throws -> ClassType {
        let data = try JSONEncoder().encode(classType)
        return try await network.performAuthorizedRequest(
            endpoint: Endpoint.classType(.create),
            method: .POST,
            body: data,
            errorObject: APIResponseError.self
        )
    }
    
    func updateClassType(_ id: Int, _ newClassType: ClassType) async throws {
        let data = try JSONEncoder().encode(newClassType)
        _ = try await network.performAuthorizedRequest(
            endpoint: Endpoint.classType(.update(id: id)),
            method: .PUT,
            body: data,
            errorObject: APIResponseError.self
        ) as EmptyResponse
    }
    
    func deleteClassType(_ id: Int) async throws {
        _ = try await network.performAuthorizedRequest(
            endpoint: Endpoint.classType(.delete(id: id)),
            method: .DELETE,
            errorObject: APIResponseError.self
        ) as EmptyResponse
    }
    
    func instructorsForClassType(_ id: Int) async throws -> [Int] {
        return try await network.performAuthorizedRequest(
            endpoint: Endpoint.classType(.getInstructors(id: id)),
            errorObject: APIResponseError.self
        )
    }
    
    func roomsForClassType(_ id: Int) async throws -> [Int] {
        return try await network.performAuthorizedRequest(
            endpoint: Endpoint.classType(.getRooms(id: id)),
            errorObject: APIResponseError.self
        )
    }
    
    func fitnessClassesForClassType(_ id: Int) async throws -> [Int] {
        return try await network.performAuthorizedRequest(
            endpoint: Endpoint.classType(.getFitnessClasses(id: id)),
            errorObject: APIResponseError.self
        )
    }
}
