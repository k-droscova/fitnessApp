//
//  ClassTypeManager.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 04.12.2024.
//

import Foundation

protocol ClassTypeManagerFlowDelegate: NSObject {}

protocol HasClassTypeManager {
    var classTypeManager: ClassTypeManaging { get }
}

protocol ClassTypeManaging {
    var delegate: ClassTypeManagerFlowDelegate? { get set }
    var allClassTypes: [ClassType] { get }
    func fetchClassTypes() async throws
    func fetchClassTypeById(_ id: Int) async throws -> ClassType
    func fetchClassTypeByName(_ name: String) async throws -> [ClassType]
    func fetchInstructorsForClassType(_ id: Int) async throws -> [Int]
    func fetchRoomsForClassType(_ id: Int) async throws -> [Int]
    func fetchFitnessClassesForClassType(_ id: Int) async throws -> [Int]
    func createClassType(_ classType: ClassType) async throws
    func updateClassType(_ id: Int, _ newClassType: ClassType) async throws
    func deleteClassType(_ id: Int) async throws
}

final class ClassTypeManager: BaseClass, ClassTypeManaging {
    typealias FlowDelegate = ClassTypeManagerFlowDelegate
    typealias Dependencies = HasLoggerService & HasClassTypeAPIService
    
    var delegate: FlowDelegate?
    private(set) var allClassTypes: [ClassType] = []
    
    private let logger: LoggerServicing
    private let classTypeAPIService: ClassTypeAPIServicing
    
    init(dependencies: Dependencies) {
        self.logger = dependencies.logger
        self.classTypeAPIService = dependencies.classTypeAPIService
    }
    
    // MARK: - Public Interface
    
    func fetchClassTypes() async throws {
        let result = try await classTypeAPIService.allClassTypes()
        allClassTypes = result
    }
    
    func createClassType(_ classType: ClassType) async throws {
        let result = try await classTypeAPIService.postNewClassType(classType)
        allClassTypes.append(result)
    }
    
    func updateClassType(_ id: Int, _ newClassType: ClassType) async throws {
        try await classTypeAPIService.updateClassType(id, newClassType)
        guard let index = allClassTypes.firstIndex(where: { $0.classTypeId == id }) else {
            throw BaseError(
                context: .system,
                message: "Could not find class type with id \(id) during update",
                logger: self.logger
            )
        }
        allClassTypes[index] = newClassType
    }
    
    func deleteClassType(_ id: Int) async throws {
        try await classTypeAPIService.deleteClassType(id)
        allClassTypes.removeAll { $0.classTypeId == id }
    }
    
    func fetchClassTypeById(_ id: Int) async throws -> ClassType {
        return try await classTypeAPIService.classType(id)
    }
    
    func fetchClassTypeByName(_ name: String) async throws -> [ClassType] {
        return try await classTypeAPIService.classTypeByName(name)
    }
    
    func fetchInstructorsForClassType(_ id: Int) async throws -> [Int] {
        return try await classTypeAPIService.instructorsForClassType(id)
    }
    
    func fetchRoomsForClassType(_ id: Int) async throws -> [Int] {
        return try await classTypeAPIService.roomsForClassType(id)
    }
    
    func fetchFitnessClassesForClassType(_ id: Int) async throws -> [Int] {
        return try await classTypeAPIService.fitnessClassesForClassType(id)
    }
}
