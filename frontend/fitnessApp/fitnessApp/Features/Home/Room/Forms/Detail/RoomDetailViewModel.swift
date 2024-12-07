//
//  RoomDetailViewModel.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 07.12.2024.
//

import Foundation

protocol RoomDetailViewFlowDelegate: NSObject {
    func onLoadError()
    func onEditPressed(room: Room)
    func onDeleteSuccess()
    func onDeleteFailure()
    func showDeleteConfirmation(_ confirmAction: @escaping () -> Void)
}

protocol RoomDetailViewModeling: BaseClass, ObservableObject {
    var room: Room { get }
    var classTypes: [ClassType] { get }
    var pastClasses: [FitnessClass] { get }
    var futureClasses: [FitnessClass] { get }
    var isLoading: Bool { get }
    func onAppear()
    func onEditPressed()
    func onDeletePressed()
}

final class RoomDetailViewModel: BaseClass, RoomDetailViewModeling {
    typealias Dependencies = HasLoggerService & HasRoomManager & HasClassTypeManager & HasFitnessClassManager

    private let logger: LoggerServicing
    private let roomManager: RoomManaging
    private let classTypeManager: ClassTypeManaging
    private let fitnessClassManager: FitnessClassManaging
    private weak var delegate: RoomDetailViewFlowDelegate?

    @Published var room: Room
    @Published var classTypes: [ClassType] = []
    @Published var pastClasses: [FitnessClass] = []
    @Published var futureClasses: [FitnessClass] = []
    @Published var isLoading: Bool = true

    init(
        dependencies: Dependencies,
        room: Room,
        delegate: RoomDetailViewFlowDelegate? = nil
    ) {
        self.logger = dependencies.logger
        self.roomManager = dependencies.roomManager
        self.classTypeManager = dependencies.classTypeManager
        self.fitnessClassManager = dependencies.fitnessClassManager
        self.room = room
        self.delegate = delegate
    }

    func onAppear() {
        Task { @MainActor [weak self] in
            guard let self = self else { return }
            self.isLoading = true
            defer { self.isLoading = false }

            guard let _ = self.room.roomId else {
                self.delegate?.onLoadError()
                return
            }

            do {
                try await self.fetchAllDetails()
            } catch {
                delegate?.onLoadError()
            }
        }
    }

    func onEditPressed() {
        delegate?.onEditPressed(room: room)
    }

    func onDeletePressed() {
        delegate?.showDeleteConfirmation { [weak self] in
            guard let self = self else { return }
            Task {
                do {
                    guard let id = self.room.roomId else {
                        self.delegate?.onDeleteFailure()
                        return
                    }
                    try await self.roomManager.deleteRoom(id)
                    self.delegate?.onDeleteSuccess()
                } catch {
                    self.delegate?.onDeleteFailure()
                }
            }
        }
    }

    // MARK: - Private Helpers

    private func fetchAllDetails() async throws {
        try await withThrowingTaskGroup(of: Void.self) { group in
            group.addTask {
                try await self.fetchClassTypes()
            }
            group.addTask {
                try await self.fetchClasses()
            }
            try await group.waitForAll()
        }
    }

    private func fetchClassTypes() async throws {
        let fetchedClassTypes = try await withThrowingTaskGroup(of: ClassType.self) { group -> [ClassType] in
            for classTypeId in room.classTypes {
                group.addTask {
                    try await self.classTypeManager.fetchClassTypeById(classTypeId)
                }
            }
            return try await group.reduce(into: [ClassType]()) { $0.append($1) }
        }
        DispatchQueue.main.async { [weak self] in
            self?.classTypes = fetchedClassTypes
        }
    }

    private func fetchClasses() async throws {
        let fetchedClasses = try await withThrowingTaskGroup(of: FitnessClass.self) { group -> [FitnessClass] in
            for fitnessClassId in room.classes {
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
