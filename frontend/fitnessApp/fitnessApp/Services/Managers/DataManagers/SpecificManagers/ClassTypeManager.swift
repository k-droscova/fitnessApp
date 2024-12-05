//
//  ClassTypeManager.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 04.12.2024.
//

import Foundation

protocol ClassTypeManagerFlowDelegate: NSObject {
    func onClassTypeCreated()
    func onClassTypeUpdated()
    func onClassTypeDeleted()
    func onError(_ error: Error)
}

protocol HasClassTypeManager {
    var classTypeManager: any ClassTypeManaging { get }
}

protocol ClassTypeManaging {
    var delegate: ClassTypeManagerFlowDelegate? { get set }
    var allClassTypes: [ClassType] { get }
    func fetchClassTypes() async
    func fetchClassTypeById(_ id: Int) async -> ClassType?
    func fetchClassTypeByName(_ name: String) async -> [ClassType]
    func fetchInstructorsForClassType(_ id: Int) async -> [Int]
    func fetchRoomsForClassType(_ id: Int) async -> [Int]
    func fetchFitnessClassesForClassType(_ id: Int) async -> [Int]
    func createClassType(_ classType: ClassType) async
    func updateClassType(_ id: Int, _ newClassType: ClassType) async
    func deleteClassType(_ id: Int) async
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
    
    func fetchClassTypes() async {
        do {
            let result = try await classTypeAPIService.allClassTypes()
            allClassTypes = result
        } catch {
            delegate?.onError(error)
        }
    }
    
    func createClassType(_ classType: ClassType) async {
        do {
            let result = try await classTypeAPIService.postNewClassType(classType)
            allClassTypes.append(result)
            delegate?.onClassTypeCreated()
            return
        } catch {
            delegate?.onError(error)
        }
    }
    
    func updateClassType(_ id: Int, _ newClassType: ClassType) async {
        do {
            try await classTypeAPIService.updateClassType(id, newClassType)
            guard let index = allClassTypes.firstIndex(where: { $0.id == id }) else {
                throw BaseError(
                    context: .system,
                    message: "Could not find class type with id \(id) during update",
                    logger: self.logger
                )
            }
            allClassTypes[index] = newClassType
        } catch {
            delegate?.onError(error)
        }
    }
    
    func deleteClassType(_ id: Int) async {
        do {
            try await classTypeAPIService.deleteClassType(id)
            allClassTypes.removeAll { $0.id == id }
            delegate?.onClassTypeDeleted()
        } catch {
            delegate?.onError(error)
        }
    }
    
    func fetchClassTypeById(_ id: Int) async -> ClassType? {
        do {
            return try await classTypeAPIService.classType(id)
        } catch {
            delegate?.onError(error)
            return nil
        }
    }
    
    func fetchClassTypeByName(_ name: String) async -> [ClassType] {
        do {
            return try await classTypeAPIService.classTypeByName(name)
        } catch {
            delegate?.onError(error)
            return []
        }
    }
    
    func fetchInstructorsForClassType(_ id: Int) async -> [Int] {
        do {
            return try await classTypeAPIService.instructorsForClassType(id)
        } catch {
            delegate?.onError(error)
            return []
        }
    }
    
    func fetchRoomsForClassType(_ id: Int) async -> [Int] {
        do {
            return try await classTypeAPIService.roomsForClassType(id)
        } catch {
            delegate?.onError(error)
            return []
        }
    }
    
    func fetchFitnessClassesForClassType(_ id: Int) async -> [Int] {
        do {
            return try await classTypeAPIService.fitnessClassesForClassType(id)
        } catch {
            delegate?.onError(error)
            return []
        }
    }
}
