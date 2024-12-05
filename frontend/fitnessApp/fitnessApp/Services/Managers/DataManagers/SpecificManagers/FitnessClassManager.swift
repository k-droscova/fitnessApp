//
//  FitnessClassManager.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 04.12.2024.
//

import Foundation

protocol FitnessClassManagerFlowDelegate: NSObject {
    func onFitnessClassCreated()
    func onFitnessClassUpdated()
    func onFitnessClassDeleted()
    func onError(_ error: Error)
}

protocol HasFitnessClassManager {
    var fitnessClassManager: any FitnessClassManaging { get }
}

protocol FitnessClassManaging {
    var delegate: FitnessClassManagerFlowDelegate? { get set }
    var allFitnessClasses: [FitnessClass] { get }
    func fetchFitnessClasses() async
    func fetchFitnessClassById(_ id: Int) async -> FitnessClass?
    func fetchFitnessClassesByFilter(date: String?, startTime: String?, endTime: String?, roomId: Int?) async -> [FitnessClass]
    func fetchTraineesForFitnessClass(_ id: Int) async -> [Int]
    func createFitnessClass(_ fitnessClass: FitnessClass) async
    func updateFitnessClass(_ id: Int, _ newFitnessClass: FitnessClass) async
    func deleteFitnessClass(_ id: Int) async
    func addTraineeToFitnessClass(_ id: Int, _ traineeId: Int) async
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
    
    // MARK: - Public Interface
    
    func fetchFitnessClasses() async {
        do {
            let result = try await fitnessClassAPIService.allFitnessClasses()
            allFitnessClasses = result
        } catch {
            delegate?.onError(error)
        }
    }
    
    func fetchFitnessClassById(_ id: Int) async -> FitnessClass? {
        do {
            return try await fitnessClassAPIService.fitnessClass(id)
        } catch {
            delegate?.onError(error)
            return nil
        }
    }
    
    func fetchFitnessClassesByFilter(date: String?, startTime: String?, endTime: String?, roomId: Int?) async -> [FitnessClass] {
        do {
            return try await fitnessClassAPIService.fitnessClassesByFilter(
                date: date,
                startTime: startTime,
                endTime: endTime,
                roomId: roomId
            )
        } catch {
            delegate?.onError(error)
            return []
        }
    }
    
    func fetchTraineesForFitnessClass(_ id: Int) async -> [Int] {
        do {
            return try await fitnessClassAPIService.getTraineesForFitnessClass(id)
        } catch {
            delegate?.onError(error)
            return []
        }
    }
    
    func createFitnessClass(_ fitnessClass: FitnessClass) async {
        do {
            try await fitnessClassAPIService.postNewFitnessClass(fitnessClass)
            allFitnessClasses.append(fitnessClass)
            delegate?.onFitnessClassCreated()
        } catch {
            delegate?.onError(error)
        }
    }
    
    func updateFitnessClass(_ id: Int, _ newFitnessClass: FitnessClass) async {
        do {
            try await fitnessClassAPIService.updateFitnessClass(id, newFitnessClass)
            guard let index = allFitnessClasses.firstIndex(where: { $0.id == id }) else {
                throw BaseError(
                    context: .system,
                    message: "Fitness class with id \(id) not found in local cache",
                    logger: self.logger
                )
            }
            allFitnessClasses[index] = newFitnessClass
            delegate?.onFitnessClassUpdated()
        } catch {
            delegate?.onError(error)
        }
    }
    
    func deleteFitnessClass(_ id: Int) async {
        do {
            try await fitnessClassAPIService.deleteFitnessClass(id)
            allFitnessClasses.removeAll { $0.id == id }
            delegate?.onFitnessClassDeleted()
        } catch {
            delegate?.onError(error)
        }
    }
    
    func addTraineeToFitnessClass(_ id: Int, _ traineeId: Int) async {
        do {
            try await fitnessClassAPIService.addTraineeToFitnessClass(id, traineeId)
        } catch {
            delegate?.onError(error)
        }
    }
}
