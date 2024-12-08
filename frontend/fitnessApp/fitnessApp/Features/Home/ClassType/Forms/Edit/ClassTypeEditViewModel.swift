//
//  ClassTypeEditViewModel.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 06.12.2024.
//

import Foundation

protocol ClassTypeEditViewFlowDelegate: NSObject {
    func onLoadError()
    func onEditViewDismissed()
    func onUpdateSuccess()
    func onUpdateFailure(message: String)
}

protocol ClassTypeEditViewModeling: BaseClass, ObservableObject {
    var classTypeName: String { get set }
    var instructors: [SelectableItem<Instructor>] { get set }
    var rooms: [SelectableItem<Room>] { get set }
    var isLoading: Bool { get set }
    func onAppear()
    func onDisappear()
    func onSavePressed()
}

final class ClassTypeEditViewModel: BaseClass, ClassTypeEditViewModeling {
    typealias Dependencies = HasLoggerService & HasClassTypeManager & HasInstructorManager & HasRoomManager

    private let logger: LoggerServicing
    private let classTypeManager: ClassTypeManaging
    private let instructorManager: InstructorManaging
    private let roomManager: RoomManaging
    private weak var delegate: ClassTypeEditViewFlowDelegate?
    private let classType: ClassType

    @Published var isLoading: Bool = true
    @Published var classTypeName: String
    @Published var instructors: [SelectableItem<Instructor>] = []
    @Published var rooms: [SelectableItem<Room>] = []

    init(dependencies: Dependencies, classType: ClassType, delegate: ClassTypeEditViewFlowDelegate? = nil) {
        self.logger = dependencies.logger
        self.instructorManager = dependencies.instructorManager
        self.roomManager = dependencies.roomManager
        self.classTypeManager = dependencies.classTypeManager
        self.delegate = delegate
        self.classType = classType
        self.classTypeName = classType.name
    }

    func onAppear() {
        Task { @MainActor [weak self] in
            guard let self = self else { return }
            self.isLoading = true
            defer { self.isLoading = false }

            do {
                try await self.fetchAllData()
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

            guard !self.classTypeName.isEmpty else {
                self.delegate?.onUpdateFailure(message: "Class name cannot be empty")
                return
            }
            
            guard let id = self.classType.classTypeId else {
                self.delegate?.onUpdateFailure(message: "Something went wrong")
                return
            }

            do {
                let selectedInstructorIds = self.instructors.filter { $0.isSelected }.map { $0.item.instructorId! }
                let selectedRoomIds = self.rooms.filter { $0.isSelected }.map { $0.item.roomId! }

                let updatedClassType = ClassType(
                    classTypeId: self.classType.classTypeId,
                    name: self.classTypeName,
                    instructors: selectedInstructorIds,
                    rooms: selectedRoomIds
                )

                try await self.classTypeManager.updateClassType(id, updatedClassType)
                self.delegate?.onUpdateSuccess()
            } catch let baseError as BaseError {
                delegate?.onUpdateFailure(message: baseError.message)
            } catch {
                delegate?.onUpdateFailure(message: error.localizedDescription)
            }
        }
    }

    // MARK: - Private Helpers

    private func fetchAllData() async throws {
        try await withThrowingTaskGroup(of: Void.self) { group in
            group.addTask { [weak self] in
                guard let self = self else { return }
                try await self.fetchInstructors()
            }
            group.addTask { [weak self] in
                guard let self = self else { return }
                try await self.fetchRooms()
            }
            try await group.waitForAll()
        }
    }

    private func fetchInstructors() async throws {
        try await instructorManager.fetchInstructors()
        let fetchedInstructors = instructorManager.allInstructors.map { instructor in
            SelectableItem(
                item: instructor,
                isSelected: classType.instructors.contains { $0 == instructor.instructorId },
                itemDescription: "\(instructor.name) \(instructor.surname)"
            )
        }
        DispatchQueue.main.async { [weak self] in
            self?.instructors = fetchedInstructors
        }
    }

    private func fetchRooms() async throws {
        try await roomManager.fetchRooms()
        let fetchedRooms = roomManager.allRooms.map { room in
            SelectableItem(
                item: room,
                isSelected: classType.rooms.contains { $0 == room.roomId },
                itemDescription: "Room \(room.roomId ?? 0)"
            )
        }
        DispatchQueue.main.async { [weak self] in
            self?.rooms = fetchedRooms
        }
    }
}
