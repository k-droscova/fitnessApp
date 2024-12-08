//
//  FitnessClassManager.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 04.12.2024.
//

import Foundation

protocol FitnessClassManagerFlowDelegate: NSObject {}

protocol HasFitnessClassManager {
    var fitnessClassManager: FitnessClassManaging { get }
}

protocol FitnessClassManaging {
    var delegate: FitnessClassManagerFlowDelegate? { get set }
    var allFitnessClasses: [FitnessClass] { get }
    func fetchFitnessClasses() async throws
    func fetchFitnessClassById(_ id: Int) async throws -> FitnessClass
    func fetchFitnessClassesByFilter(date: String?, startTime: String?, endTime: String?, roomId: Int?) async throws -> [FitnessClass]
    func fetchTraineesForFitnessClass(_ id: Int) async throws -> [Int]
    func createFitnessClass(_ fitnessClass: FitnessClass) async throws
    func updateFitnessClass(_ id: Int, _ newFitnessClass: FitnessClass) async throws
    func deleteFitnessClass(_ id: Int) async throws
    func addTraineeToFitnessClass(_ id: Int, _ traineeId: Int) async throws
    func deleteTraineeFromFitnessClass(_ id: Int, _ traineeId: Int) async throws
}

final class FitnessClassManager: BaseClass, FitnessClassManaging {
    typealias Dependencies = HasLoggerService & HasFitnessClassAPIService
    
    weak var delegate: FitnessClassManagerFlowDelegate?
    private(set) var allFitnessClasses: [FitnessClass] = []
    
    private let logger: LoggerServicing
    private let fitnessClassAPIService: FitnessClassAPIServicing
    
    init(dependencies: Dependencies) {
        self.logger = dependencies.logger
        self.fitnessClassAPIService = dependencies.fitnessClassAPIService
    }
    
    func fetchFitnessClasses() async throws {
        allFitnessClasses = try await fitnessClassAPIService.allFitnessClasses()
    }
    
    func fetchFitnessClassById(_ id: Int) async throws -> FitnessClass {
        return try await fitnessClassAPIService.fitnessClass(id)
    }
    
    func fetchFitnessClassesByFilter(date: String?, startTime: String?, endTime: String?, roomId: Int?) async throws -> [FitnessClass] {
        return try await fitnessClassAPIService.fitnessClassesByFilter(date: date, startTime: startTime, endTime: endTime, roomId: roomId)
    }
    
    func fetchTraineesForFitnessClass(_ id: Int) async throws -> [Int] {
        return try await fitnessClassAPIService.getTraineesForFitnessClass(id)
    }
    
    func createFitnessClass(_ fitnessClass: FitnessClass) async throws {
        try await fitnessClassAPIService.postNewFitnessClass(fitnessClass)
        allFitnessClasses.append(fitnessClass)
    }
    
    func updateFitnessClass(_ id: Int, _ newFitnessClass: FitnessClass) async throws {
        try await fitnessClassAPIService.updateFitnessClass(id, newFitnessClass)
        guard let index = allFitnessClasses.firstIndex(where: { $0.fitnessClassId == id }) else {
            throw BaseError(
                context: .system,
                message: "Fitness class with id \(id) not found in local cache",
                logger: logger
            )
        }
        allFitnessClasses[index] = newFitnessClass
    }
    
    func deleteFitnessClass(_ id: Int) async throws {
        try await fitnessClassAPIService.deleteFitnessClass(id)
        allFitnessClasses.removeAll { $0.fitnessClassId == id }
    }
    
    func addTraineeToFitnessClass(_ id: Int, _ traineeId: Int) async throws {
        try await fitnessClassAPIService.addTraineeToFitnessClass(id, traineeId)
    }
    
    func deleteTraineeFromFitnessClass(_ id: Int, _ traineeId: Int) async throws {
        try await fitnessClassAPIService.deleteTraineeFromFitnessClass(id, traineeId)
    }
}
