//
//  TraineeDetailViewModel.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 07.12.2024.
//

import Foundation

protocol TraineeDetailViewFlowDelegate: NSObject {
    func onLoadError()
    func onEditPressed(trainee: Trainee)
    func onDeleteSuccess()
    func onDeleteFailure()
    func showDeleteConfirmation(_ confirmAction: @escaping () -> Void)
    func onRegisterPressed(trainee: Trainee)
    func onUnregisterPressed(trainee: Trainee)
}

protocol TraineeDetailViewModeling: BaseClass, ObservableObject {
    var trainee: Trainee { get }
    var pastClasses: [FitnessClass] { get }
    var futureClasses: [FitnessClass] { get }
    var isUnregisteredButtonDisabled: Bool { get }
    func onAppear()
    func onEditPressed()
    func onDeletePressed()
    func onRegisterPressed()
    func onUnregisterPressed()
}

final class TraineeDetailViewModel: BaseClass, TraineeDetailViewModeling {
    typealias Dependencies = HasLoggerService & HasTraineeManager & HasFitnessClassManager
    
    private let logger: LoggerServicing
    private let traineeManager: TraineeManaging
    private let fitnessClassManager: FitnessClassManaging
    private weak var delegate: TraineeDetailViewFlowDelegate?
    
    @Published var trainee: Trainee
    @Published var pastClasses: [FitnessClass] = []
    @Published var futureClasses: [FitnessClass] = []
    @Published var isLoading: Bool = true
    var isUnregisteredButtonDisabled: Bool { futureClasses.isEmpty }
    
    init(
        dependencies: Dependencies,
        trainee: Trainee,
        delegate: TraineeDetailViewFlowDelegate? = nil
    ) {
        self.logger = dependencies.logger
        self.traineeManager = dependencies.traineeManager
        self.fitnessClassManager = dependencies.fitnessClassManager
        self.trainee = trainee
        self.delegate = delegate
    }
    
    func onAppear() {
        Task { @MainActor [weak self] in
            guard let self = self else { return }
            self.isLoading = true
            defer { self.isLoading = false }
            
            guard self.trainee.traineeId != nil else {
                self.delegate?.onLoadError()
                return
            }
            
            do {
                try await self.fetchClasses()
            } catch {
                self.delegate?.onLoadError()
            }
        }
    }
    
    func onEditPressed() {
        delegate?.onEditPressed(trainee: trainee)
    }
    
    func onDeletePressed() {
        delegate?.showDeleteConfirmation { [weak self] in
            guard let self = self else { return }
            Task {
                do {
                    guard let id = self.trainee.traineeId else {
                        self.delegate?.onDeleteFailure()
                        return
                    }
                    try await self.traineeManager.deleteTrainee(id)
                    self.delegate?.onDeleteSuccess()
                } catch {
                    self.delegate?.onDeleteFailure()
                }
            }
        }
    }
    
    func onRegisterPressed() {
        delegate?.onRegisterPressed(trainee: trainee)
    }
    
    func onUnregisterPressed() {
        delegate?.onUnregisterPressed(trainee: trainee)
    }
    
    // MARK: - Private Helpers
    
    private func fetchClasses() async throws {
        guard let id = self.trainee.traineeId else {
            self.delegate?.onLoadError()
            return
        }
        let updatedTrainee = try await traineeManager.fetchTraineeById(id)
        let fetchedClasses = try await withThrowingTaskGroup(of: FitnessClass.self) { group -> [FitnessClass] in
            for fitnessClassId in updatedTrainee.classes {
                group.addTask {
                    try await self.fitnessClassManager.fetchFitnessClassById(fitnessClassId)
                }
            }
            return try await group.reduce(into: [FitnessClass]()) { $0.append($1) }
        }
        
        DispatchQueue.main.async { [weak self] in
            guard let self = self else { return }
            self.trainee = updatedTrainee
            self.pastClasses = fetchedClasses
                .filter { !Calendar.current.isDateTimeInFuture($0.dateTime) }
                .sorted(by: { $0.dateTime > $1.dateTime })
            
            self.futureClasses = fetchedClasses
                .filter { Calendar.current.isDateTimeInFuture($0.dateTime) }
                .sorted(by: { $0.dateTime < $1.dateTime })
        }
    }
}
