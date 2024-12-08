//
//  ClassTypeDetailViewModel.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 05.12.2024.
//

import Foundation

protocol ClassTypeDetailViewFlowDelegate: NSObject {
    func onLoadError()
    func onEditPressed(classType: ClassType)
    func onDeleteSuccess()
    func onDeleteFailure()
    func showDeleteConfirmation(_ confirmAction: @escaping () -> Void)
    func onDetailDismissed()
}

protocol ClassTypeDetailViewModeling: BaseClass, ObservableObject {
    var classType: ClassType { get set }
    var classTypeName: String { get set }
    var instructors: [Instructor] { get set }
    var rooms: [Room] { get set }
    var fitnessClasses: [FitnessClass] { get set }
    var isLoading: Bool { get set }
    func onAppear()
    func onDisappear()
    func deleteButtonPressed()
    func editButtonPressed()
}

final class ClassTypeDetailViewModel: BaseClass, ClassTypeDetailViewModeling {
    typealias Dependencies = HasLoggerService & HasClassTypeManager & HasInstructorManager & HasRoomManager & HasFitnessClassManager

    private let logger: LoggerServicing
    private let classTypeManager: ClassTypeManaging
    private let instructorManager: InstructorManaging
    private let roomManager: RoomManaging
    private let fitnessClassManager: FitnessClassManaging
    private weak var delegate: ClassTypeDetailViewFlowDelegate?

    @Published var isLoading: Bool = true
    @Published var classType: ClassType
    @Published var classTypeName: String = ""
    @Published var instructors: [Instructor] = []
    @Published var rooms: [Room] = []
    @Published var fitnessClasses: [FitnessClass] = []

    init(dependencies: Dependencies, classType: ClassType, delegate: ClassTypeDetailViewFlowDelegate? = nil) {
        self.logger = dependencies.logger
        self.instructorManager = dependencies.instructorManager
        self.roomManager = dependencies.roomManager
        self.fitnessClassManager = dependencies.fitnessClassManager
        self.classTypeManager = dependencies.classTypeManager
        self.classType = classType
        self.delegate = delegate
    }

    func onAppear() {
        Task { @MainActor [weak self] in
            guard let self = self else { return }
            self.isLoading = true
            defer { self.isLoading = false }
            guard self.classType.classTypeId != nil else {
                self.delegate?.onLoadError()
                return
            }
            self.classTypeName = self.classType.name
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

    func deleteButtonPressed() {
        delegate?.showDeleteConfirmation { [weak self] in
            guard let self = self else { return }
            Task { [weak self] in
                guard let self else { return }
                guard let id = self.classType.classTypeId else {
                    self.delegate?.onDeleteFailure()
                    return
                }
                do {
                    try await self.classTypeManager.deleteClassType(id)
                    self.delegate?.onDeleteSuccess()
                } catch {
                    self.delegate?.onDeleteFailure()
                }
            }
        }
    }

    func editButtonPressed() {
        delegate?.onEditPressed(classType: classType)
    }

    // MARK: - Private Helpers

    private func fetchAllDetails() async throws {
        try await withThrowingTaskGroup(of: Void.self) { group in
            group.addTask {
                try await self.fetchInstructorsForClassType()
            }
            group.addTask {
                try await self.fetchRoomsForClassType()
            }
            group.addTask {
                try await self.fetchFitnessClassesForClassType()
            }
            try await group.waitForAll()
        }
    }

    private func fetchInstructorsForClassType() async throws {
        let fetchedInstructors = try await withThrowingTaskGroup(of: Instructor.self) { group -> [Instructor] in
            for instructorId in classType.instructors {
                group.addTask {
                    try await self.instructorManager.fetchInstructorById(instructorId)
                }
            }
            return try await group.reduce(into: [Instructor]()) { $0.append($1) }
        }
        DispatchQueue.main.async { [weak self] in
            self?.instructors = fetchedInstructors
        }
    }

    private func fetchRoomsForClassType() async throws {
        let fetchedRooms = try await withThrowingTaskGroup(of: Room.self) { group -> [Room] in
            for roomId in classType.rooms {
                group.addTask {
                    try await self.roomManager.fetchRoomById(roomId)
                }
            }
            return try await group.reduce(into: [Room]()) { $0.append($1) }
        }
        DispatchQueue.main.async { [weak self] in
            self?.rooms = fetchedRooms
        }
    }

    private func fetchFitnessClassesForClassType() async throws {
        let fetchedFitnessClasses = try await withThrowingTaskGroup(of: FitnessClass.self) { group -> [FitnessClass] in
            for fitnessClassId in classType.fitnessClasses {
                group.addTask {
                    try await self.fitnessClassManager.fetchFitnessClassById(fitnessClassId)
                }
            }
            return try await group.reduce(into: [FitnessClass]()) { $0.append($1) }
        }
        DispatchQueue.main.async { [weak self] in
            self?.fitnessClasses = fetchedFitnessClasses
        }
    }
}
