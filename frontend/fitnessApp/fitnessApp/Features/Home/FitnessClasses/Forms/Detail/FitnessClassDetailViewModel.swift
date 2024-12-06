//
//  FitnessClassDetailViewModel.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 06.12.2024.
//

import Foundation

protocol FitnessClassDetailFlowDelegate: NSObject {
    func onLoadError()
    func onEditPressed(fitnessClass: FitnessClass)
    func onDeleteSuccess()
    func onDeleteFailure()
    func showDeleteConfirmation(_ confirmAction: @escaping () -> Void)
}

protocol FitnessClassDetailViewModeling: BaseClass, ObservableObject {
    var isEditingEnabled: Bool { get }
    var dateTimeString: String { get }
    var classTypeName: String { get }
    var instructor: Instructor? { get }
    var roomId: Int { get }
    var capacity: Int { get }
    var occupancy: Int { get }
    var trainees: [Trainee] { get }
    var isLoading: Bool { get set }
    func onAppear()
    func onEditPressed()
    func onDeletePressed()
}

final class FitnessClassDetailViewModel: BaseClass, FitnessClassDetailViewModeling {
    typealias Dependencies = HasLoggerService & HasFitnessClassManager & HasClassTypeManager & HasInstructorManager & HasTraineeManager
    
    private let logger: LoggerServicing
    private let fitnessClassManager: FitnessClassManaging
    private let classTypeManager: ClassTypeManaging
    private let instructorManager: InstructorManaging
    private let traineeManager: TraineeManaging
    private let fitnessClass: FitnessClass
    private weak var delegate: FitnessClassDetailFlowDelegate?
    
    @Published var isLoading: Bool = true
    @Published private(set) var classTypeName: String = ""
    @Published private(set) var instructor: Instructor? = nil
    @Published private(set) var trainees: [Trainee] = []
    
    var roomId: Int {
        fitnessClass.room
    }
    
    var capacity: Int {
        fitnessClass.capacity
    }
    
    var occupancy: Int {
        fitnessClass.trainees.count
    }
    
    var isEditingEnabled: Bool {
        Calendar.current.isDateInFuture(fitnessClass.dateTime)
    }
    
    var dateTimeString: String {
        let formatter = DateFormatter()
        formatter.dateStyle = .medium
        formatter.timeStyle = .short
        return formatter.string(from: fitnessClass.dateTime)
    }
    
    init(
        dependencies: Dependencies,
        fitnessClass: FitnessClass,
        delegate: FitnessClassDetailFlowDelegate? = nil
    ) {
        self.logger = dependencies.logger
        self.fitnessClassManager = dependencies.fitnessClassManager
        self.classTypeManager = dependencies.classTypeManager
        self.instructorManager = dependencies.instructorManager
        self.traineeManager = dependencies.traineeManager
        self.fitnessClass = fitnessClass
        self.delegate = delegate
    }
    
    func onAppear() {
        Task { @MainActor [weak self] in
            guard let self = self else { return }
            self.isLoading = true
            defer { self.isLoading = false }
            do {
                try await self.fetchDetails()
            } catch {
                self.delegate?.onLoadError()
            }
        }
    }
    
    func onEditPressed() {
        delegate?.onEditPressed(fitnessClass: fitnessClass)
    }
    
    func onDeletePressed() {
        delegate?.showDeleteConfirmation { [weak self] in
            guard let self = self else { return }
            Task { [weak self] in
                guard let self else { return }
                guard let id = self.fitnessClass.fitnessClassId else {
                    self.delegate?.onDeleteFailure()
                    return
                }
                do {
                    try await self.fitnessClassManager.deleteFitnessClass(id)
                    self.delegate?.onDeleteSuccess()
                } catch {
                    self.delegate?.onDeleteFailure()
                }
            }
        }
    }
    
    // MARK: - Private Helpers
    
    private func fetchDetails() async throws {
        try await withThrowingTaskGroup(of: Void.self) { group in
            group.addTask {
                try await self.fetchClassTypeName()
            }
            group.addTask {
                try await self.fetchInstructor()
            }
            group.addTask {
                try await self.fetchTrainees()
            }
            try await group.waitForAll()
        }
    }
    
    private func fetchClassTypeName() async throws {
        let classType = try await classTypeManager.fetchClassTypeById(fitnessClass.classType)
        DispatchQueue.main.async { [weak self] in
            self?.classTypeName = classType.name
        }
    }
    
    private func fetchInstructor() async throws {
        let fetchedInstructor = try await instructorManager.fetchInstructorById(fitnessClass.instructor)
        DispatchQueue.main.async { [weak self] in
            self?.instructor = fetchedInstructor
        }
    }
    
    private func fetchTrainees() async throws {
        guard let fitnessClassId = fitnessClass.fitnessClassId else {
            delegate?.onLoadError()
            return
        }
        let traineeIds = fitnessClass.trainees
        let fetchedTrainees = try await withThrowingTaskGroup(of: Trainee.self) { group -> [Trainee] in
            for traineeId in traineeIds {
                group.addTask {
                    return try await self.traineeManager.fetchTraineeById(traineeId)
                }
            }
            
            var trainees: [Trainee] = []
            for try await trainee in group {
                trainees.append(trainee)
            }
            return trainees
        }
        
        DispatchQueue.main.async { [weak self] in
            self?.trainees = fetchedTrainees
        }
    }
}
