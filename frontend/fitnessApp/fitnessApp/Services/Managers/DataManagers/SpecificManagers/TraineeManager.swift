//
//  TraineeManager.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 04.12.2024.
//

import Foundation

protocol TraineeManagerFlowDelegate: NSObject {
    func onTraineeCreated()
    func onTraineeUpdated()
    func onTraineeDeleted()
    func onError(_ error: Error)
}

protocol HasTraineeManager {
    var traineeManager: any TraineeManaging { get }
}

protocol TraineeManaging {
    var delegate: TraineeManagerFlowDelegate? { get set }
    var allTrainees: [Trainee] { get }
    func fetchAllTrainees() async
    func fetchTraineeById(_ id: Int) async -> Trainee?
    func createTrainee(_ trainee: Trainee) async
    func updateTrainee(_ id: Int, with newTrainee: Trainee) async
    func deleteTrainee(_ id: Int) async
    func fetchTraineesByFitnessClassId(_ fitnessClassId: Int) async -> [Trainee]
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
    
    func fetchAllTrainees() async {
        do {
            let result = try await traineeAPIService.getAllTrainees()
            allTrainees = result
        } catch {
            delegate?.onError(error)
        }
    }
    
    func fetchTraineeById(_ id: Int) async -> Trainee? {
        do {
            return try await traineeAPIService.getTraineeById(id)
        } catch {
            delegate?.onError(error)
            return nil
        }
    }
    
    func createTrainee(_ trainee: Trainee) async {
        do {
            try await traineeAPIService.createTrainee(trainee)
            allTrainees.append(trainee)
            delegate?.onTraineeCreated()
        } catch {
            delegate?.onError(error)
        }
    }
    
    func updateTrainee(_ id: Int, with newTrainee: Trainee) async {
        do {
            try await traineeAPIService.updateTrainee(id, with: newTrainee)
            guard let index = allTrainees.firstIndex(where: { $0.id == id }) else {
                throw BaseError(
                    context: .system,
                    message: "Trainee with id \(id) not found in local cache",
                    logger: self.logger
                )
            }
            allTrainees[index] = newTrainee
            delegate?.onTraineeUpdated()
        } catch {
            delegate?.onError(error)
        }
    }
    
    func deleteTrainee(_ id: Int) async {
        do {
            try await traineeAPIService.deleteTrainee(id)
            allTrainees.removeAll { $0.id == id }
            delegate?.onTraineeDeleted()
        } catch {
            delegate?.onError(error)
        }
    }
    
    func fetchTraineesByFitnessClassId(_ fitnessClassId: Int) async -> [Trainee] {
        do {
            return try await traineeAPIService.getTraineesByFitnessClassId(fitnessClassId)
        } catch {
            delegate?.onError(error)
            return []
        }
    }
}
