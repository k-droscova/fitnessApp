//
//  TraineeEditViewModel.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 07.12.2024.
//

import Foundation

protocol TraineeEditViewFlowDelegate: NSObject {
    func onLoadError()
    func onEditViewDismissed()
    func onUpdateSuccess()
    func onUpdateFailure(message: String)
}

protocol TraineeEditViewModeling: BaseClass, ObservableObject {
    var name: String { get set }
    var surname: String { get set }
    var email: String { get set }
    var isLoading: Bool { get }
    func onAppear()
    func onDisappear()
    func onSavePressed()
}

final class TraineeEditViewModel: BaseClass, TraineeEditViewModeling {
    typealias Dependencies = HasLoggerService & HasTraineeManager

    private let logger: LoggerServicing
    private let traineeManager: TraineeManaging
    private weak var delegate: TraineeEditViewFlowDelegate?
    private let trainee: Trainee

    @Published var name: String
    @Published var surname: String
    @Published var email: String
    @Published var isLoading: Bool = false

    init(dependencies: Dependencies, trainee: Trainee, delegate: TraineeEditViewFlowDelegate? = nil) {
        self.logger = dependencies.logger
        self.traineeManager = dependencies.traineeManager
        self.delegate = delegate
        self.trainee = trainee
        self.name = trainee.name
        self.surname = trainee.surname
        self.email = trainee.email
    }
    
    func onAppear() {
        return
    }

    func onDisappear() {
        delegate?.onEditViewDismissed()
    }

    func onSavePressed() {
        Task { @MainActor [weak self] in
            guard let self = self else { return }
            self.isLoading = true
            defer { self.isLoading = false }

            guard !self.name.isEmpty, !self.surname.isEmpty, !self.email.isEmpty else {
                self.delegate?.onUpdateFailure(message: "All fields must be filled in")
                return
            }

            guard self.email.isValidEmail else {
                self.delegate?.onUpdateFailure(message: "Invalid email format")
                return
            }

            guard let traineeId = self.trainee.traineeId else {
                self.delegate?.onUpdateFailure(message: "Something went wrong")
                return
            }

            do {
                let updatedTrainee = Trainee(
                    traineeId: traineeId,
                    email: self.email,
                    name: self.name,
                    surname: self.surname,
                    classes: self.trainee.classes // Preserve existing classes
                )

                try await self.traineeManager.updateTrainee(traineeId, with: updatedTrainee)
                self.delegate?.onUpdateSuccess()
            } catch let baseError as BaseError {
                delegate?.onUpdateFailure(message: baseError.message)
            } catch {
                delegate?.onUpdateFailure(message: error.localizedDescription)
            }
        }
    }
}
