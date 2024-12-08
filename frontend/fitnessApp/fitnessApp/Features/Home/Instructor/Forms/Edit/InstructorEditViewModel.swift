//
//  EditViewModel.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 07.12.2024.
//

import Foundation

protocol InstructorEditViewFlowDelegate: NSObject {
    func onLoadError()
    func onEditViewDismissed()
    func onUpdateSuccess()
    func onUpdateFailure(message: String)
}

protocol InstructorEditViewModeling: BaseClass, ObservableObject {
    var name: String { get set }
    var surname: String { get set }
    var birthdate: Date? { get set }
    var specializations: [SelectableItem<ClassType>] { get set }
    var isLoading: Bool { get set }
    func onAppear()
    func onDisappear()
    func onSavePressed()
}

final class InstructorEditViewModel: BaseClass, InstructorEditViewModeling {
    typealias Dependencies = HasLoggerService & HasInstructorManager & HasClassTypeManager

    private let logger: LoggerServicing
    private let instructorManager: InstructorManaging
    private let classTypeManager: ClassTypeManaging
    private weak var delegate: InstructorEditViewFlowDelegate?
    private let instructor: Instructor

    @Published var name: String
    @Published var surname: String
    @Published var birthdate: Date?
    @Published var specializations: [SelectableItem<ClassType>] = []
    @Published var isLoading: Bool = true

    init(dependencies: Dependencies, instructor: Instructor, delegate: InstructorEditViewFlowDelegate? = nil) {
        self.logger = dependencies.logger
        self.instructorManager = dependencies.instructorManager
        self.classTypeManager = dependencies.classTypeManager
        self.delegate = delegate
        self.instructor = instructor
        self.name = instructor.name
        self.surname = instructor.surname
        self.birthdate = instructor.birthDate
    }

    func onAppear() {
        Task { @MainActor [weak self] in
            guard let self = self else { return }
            self.isLoading = true
            defer { self.isLoading = false }
            
            do {
                try await self.fetchSpecializations()
            } catch {
                self.delegate?.onLoadError()
            }
        }
    }

    func onDisappear() {
        delegate?.onEditViewDismissed()
    }

    func onSavePressed() {
        Task { @MainActor [weak self] in
            guard let self = self else { return }

            guard !self.name.isEmpty, !self.surname.isEmpty, let birthdate = self.birthdate else {
                self.delegate?.onUpdateFailure(message: "All fields must be filled in")
                return
            }
            
            let selectedSpecializationIds = self.specializations.filter { $0.isSelected }.map { $0.item.classTypeId! }
            
            guard selectedSpecializationIds.count > 0 else {
                self.delegate?.onUpdateFailure(message: "Please select at least one specialization")
                return
            }
            
            guard let id = self.instructor.instructorId else {
                self.delegate?.onUpdateFailure(message: "Something went wrong")
                return
            }

            do {
                let updatedInstructor = Instructor(
                    instructorId: id,
                    name: self.name,
                    surname: self.surname,
                    birthDate: birthdate,
                    specializations: selectedSpecializationIds,
                    classes: self.instructor.classes
                )

                try await self.instructorManager.updateInstructor(id, updatedInstructor)
                self.delegate?.onUpdateSuccess()
            } catch let baseError as BaseError {
                delegate?.onUpdateFailure(message: baseError.message)
            } catch {
                delegate?.onUpdateFailure(message: error.localizedDescription)
            }
        }
    }

    // MARK: - Private Helpers

    private func fetchSpecializations() async throws {
        try await classTypeManager.fetchClassTypes()
        let fetchedSpecializations = classTypeManager.allClassTypes.map { classType in
            SelectableItem(
                item: classType,
                isSelected: instructor.specializations.contains { $0 == classType.classTypeId },
                itemDescription: classType.name
            )
        }
        DispatchQueue.main.async { [weak self] in
            self?.specializations = fetchedSpecializations
        }
    }
}
