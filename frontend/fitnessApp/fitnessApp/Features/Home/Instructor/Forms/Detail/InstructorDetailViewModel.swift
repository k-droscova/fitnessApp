//
//  InstructorDetailViewModel.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 07.12.2024.
//

import Foundation

protocol InstructorDetailViewFlowDelegate: NSObject {
    func onLoadError()
    func onEditPressed(instructor: Instructor)
    func onDeleteSuccess()
    func onDeleteFailure()
    func showDeleteConfirmation(_ confirmAction: @escaping () -> Void)
    func onDetailDismissed()
}

protocol InstructorDetailViewModeling: BaseClass, ObservableObject {
    var instructor: Instructor { get }
    var birthday: String { get }
    var specializations: [ClassType] { get }
    var pastClasses: [FitnessClass] { get }
    var futureClasses: [FitnessClass] { get }
    var isLoading: Bool { get }
    func onAppear()
    func onDisappear()
    func onEditPressed()
    func onDeletePressed()
}

final class InstructorDetailViewModel: BaseClass, InstructorDetailViewModeling {
    typealias Dependencies = HasLoggerService & HasInstructorManager & HasClassTypeManager & HasFitnessClassManager
    
    private let logger: LoggerServicing
    private let instructorManager: InstructorManaging
    private let classTypeManager: ClassTypeManaging
    private let fitnessClassManager: FitnessClassManaging
    private weak var delegate: InstructorDetailViewFlowDelegate?

    @Published var instructor: Instructor
    @Published var birthday: String = ""
    @Published var specializations: [ClassType] = []
    @Published var pastClasses: [FitnessClass] = []
    @Published var futureClasses: [FitnessClass] = []
    @Published var isLoading: Bool = true

    init(
        dependencies: Dependencies,
        instructor: Instructor,
        delegate: InstructorDetailViewFlowDelegate? = nil
    ) {
        self.logger = dependencies.logger
        self.instructorManager = dependencies.instructorManager
        self.classTypeManager = dependencies.classTypeManager
        self.fitnessClassManager = dependencies.fitnessClassManager
        self.instructor = instructor
        self.delegate = delegate
    }

    func onAppear() {
        Task { @MainActor [weak self] in
            guard let self = self else { return }
            self.isLoading = true
            defer { self.isLoading = false }
            
            guard let _ = self.instructor.instructorId else {
                self.delegate?.onLoadError()
                return
            }
            self.birthday = Date.UI.formatDate(self.instructor.birthDate)
            do {
                try await self.fetchAllDetails()
            } catch {
                delegate?.onLoadError()
            }
        }
    }
    
    func onDisappear() {
        delegate?.onDetailDismissed()
    }

    func onDeletePressed() {
        delegate?.showDeleteConfirmation { [weak self] in
            guard let self = self else { return }
            Task {
                do {
                    guard let id = self.instructor.instructorId else {
                        self.delegate?.onDeleteFailure()
                        return
                    }
                    try await self.instructorManager.deleteInstructor(id)
                    self.delegate?.onDeleteSuccess()
                } catch {
                    self.delegate?.onDeleteFailure()
                }
            }
        }
    }

    func onEditPressed() {
        delegate?.onEditPressed(instructor: instructor)
    }

    // MARK: - Private Helpers

    private func fetchAllDetails() async throws {
        try await withThrowingTaskGroup(of: Void.self) { group in
            group.addTask {
                try await self.fetchSpecializations()
            }
            group.addTask {
                try await self.fetchClasses()
            }
            try await group.waitForAll()
        }
    }

    private func fetchSpecializations() async throws {
        let fetchedSpecializations = try await withThrowingTaskGroup(of: ClassType.self) { group -> [ClassType] in
            for specializationId in instructor.specializations {
                group.addTask {
                    try await self.classTypeManager.fetchClassTypeById(specializationId)
                }
            }
            return try await group.reduce(into: [ClassType]()) { $0.append($1) }
        }
        DispatchQueue.main.async { [weak self] in
            self?.specializations = fetchedSpecializations
        }
    }

    private func fetchClasses() async throws {
        let fetchedClasses = try await withThrowingTaskGroup(of: FitnessClass.self) { group -> [FitnessClass] in
            for fitnessClassId in instructor.classes {
                group.addTask {
                    try await self.fitnessClassManager.fetchFitnessClassById(fitnessClassId)
                }
            }
            return try await group.reduce(into: [FitnessClass]()) { $0.append($1) }
        }

        DispatchQueue.main.async { [weak self] in
            self?.pastClasses = fetchedClasses
                .filter { !Calendar.current.isDateTimeInFuture($0.dateTime) }
                .sorted(by: { $0.dateTime > $1.dateTime })
            self?.futureClasses = fetchedClasses
                .filter { Calendar.current.isDateTimeInFuture($0.dateTime) }
                .sorted(by: { $0.dateTime < $1.dateTime })
        }
    }
}
