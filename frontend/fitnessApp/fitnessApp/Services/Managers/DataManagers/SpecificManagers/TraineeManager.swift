//
//  TraineeManager.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 04.12.2024.
//

import Foundation

protocol TraineeManagerFlowDelegate: NSObject {}

protocol HasTraineeManager {
    var traineeManager: TraineeManaging { get }
}

protocol TraineeManaging {
    var delegate: TraineeManagerFlowDelegate? { get set }
    var allTrainees: [Trainee] { get }
    func fetchAllTrainees() async throws
    func fetchTraineeById(_ id: Int) async throws -> Trainee
    func createTrainee(_ trainee: Trainee) async throws
    func updateTrainee(_ id: Int, with newTrainee: Trainee) async throws
    func deleteTrainee(_ id: Int) async throws
    func fetchTraineesByFitnessClassId(_ fitnessClassId: Int) async throws -> [Trainee]
}

final class TraineeManager: BaseClass, TraineeManaging {
    typealias Dependencies = HasLoggerService & HasTraineeAPIService
    
    weak var delegate: TraineeManagerFlowDelegate?
    private(set) var allTrainees: [Trainee] = []
    
    private let logger: LoggerServicing
    private let traineeAPIService: TraineeAPIServicing
    
    init(dependencies: Dependencies) {
        self.logger = dependencies.logger
        self.traineeAPIService = dependencies.traineeAPIService
    }
    
    // MARK: - Public Interface
    
    func fetchAllTrainees() async throws {
        let result = try await traineeAPIService.getAllTrainees()
        allTrainees = result
    }
    
    func fetchTraineeById(_ id: Int) async throws -> Trainee {
        return try await traineeAPIService.getTraineeById(id)
    }
    
    func createTrainee(_ trainee: Trainee) async throws {
        try await traineeAPIService.createTrainee(trainee)
        allTrainees.append(trainee)
    }
    
    func updateTrainee(_ id: Int, with newTrainee: Trainee) async throws {
        try await traineeAPIService.updateTrainee(id, with: newTrainee)
        guard let index = allTrainees.firstIndex(where: { $0.traineeId == id }) else {
            throw BaseError(
                context: .system,
                message: "Trainee with id \(id) not found in local cache",
                logger: logger
            )
        }
        allTrainees[index] = newTrainee
    }
    
    func deleteTrainee(_ id: Int) async throws {
        try await traineeAPIService.deleteTrainee(id)
        allTrainees.removeAll { $0.traineeId == id }
    }
    
    func fetchTraineesByFitnessClassId(_ fitnessClassId: Int) async throws -> [Trainee] {
        return try await traineeAPIService.getTraineesByFitnessClassId(fitnessClassId)
    }
}
