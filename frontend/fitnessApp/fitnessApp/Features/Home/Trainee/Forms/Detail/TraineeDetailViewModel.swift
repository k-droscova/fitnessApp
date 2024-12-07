//
//  TraineeDetailViewModel.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 07.12.2024.
//

import Foundation

protocol TraineeDetaulViewFlowDelegate: NSObject {
    func onLoadError()
    func onEditPressed(trainee: Trainee)
    func onDeleteSuccess()
    func onDeleteFailure()
    func showDeleteConfirmation(_ confirmAction: @escaping () -> Void)
}

protocol TraineeDetailViewModeling: BaseClass, ObservableObject {
    var trainee: Trainee { get }
    var pastClasses: [FitnessClass] { get }
    var futureClasses: [FitnessClass] { get }
}

final class TraineeDetailViewModel: BaseClass, TraineeDetailViewModeling {
    typealias Dependencies = HasLoggerService & HasTraineeManager & HasFitnessClassManager
    
    private let logger: LoggerServicing
    private let traineeManager: TraineeManaging
    private let fitnessClassManager: FitnessClassManaging
    private weak var delegate: TraineeDetaulViewFlowDelegate?

    @Published var trainee: Trainee
    @Published var pastClasses: [FitnessClass] = []
    @Published var futureClasses: [FitnessClass] = []
    @Published var isLoading: Bool = true

    init(
        dependencies: Dependencies,
        trainee: Trainee,
        delegate: TraineeDetaulViewFlowDelegate? = nil
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

            guard let _ = self.trainee.traineeId else {
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

    // MARK: - Private Helpers

    private func fetchClasses() async throws {
        let fetchedClasses = try await withThrowingTaskGroup(of: FitnessClass.self) { group -> [FitnessClass] in
            for fitnessClassId in trainee.classes {
                group.addTask {
                    try await self.fitnessClassManager.fetchFitnessClassById(fitnessClassId)
                }
            }
            return try await group.reduce(into: [FitnessClass]()) { $0.append($1) }
        }

        DispatchQueue.main.async { [weak self] in
            guard let self = self else { return }
            self.pastClasses = fetchedClasses
                .filter { !Calendar.current.isDateTimeInFuture($0.dateTime) }
                .sorted(by: { $0.dateTime > $1.dateTime })

            self.futureClasses = fetchedClasses
                .filter { Calendar.current.isDateTimeInFuture($0.dateTime) }
                .sorted(by: { $0.dateTime < $1.dateTime })
        }
    }
}
