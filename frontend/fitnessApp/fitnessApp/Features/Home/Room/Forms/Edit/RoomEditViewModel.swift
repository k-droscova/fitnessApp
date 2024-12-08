//
//  RoomEditViewModel.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 07.12.2024.
//

import Foundation

protocol RoomEditViewFlowDelegate: NSObject {
    func onLoadError()
    func onEditViewDismissed()
    func onUpdateSuccess()
    func onUpdateFailure(message: String)
}

protocol RoomEditViewModeling: BaseClass, ObservableObject {
    var maxCapacity: Int { get set }
    var classTypes: [SelectableItem<ClassType>] { get set }
    var isLoading: Bool { get set }
    func onAppear()
    func onDisappear()
    func onSavePressed()
}

final class RoomEditViewModel: BaseClass, RoomEditViewModeling {
    typealias Dependencies = HasLoggerService & HasRoomManager & HasClassTypeManager
    
    private let logger: LoggerServicing
    private let roomManager: RoomManaging
    private let classTypeManager: ClassTypeManaging
    private weak var delegate: RoomEditViewFlowDelegate?
    private let room: Room
    
    @Published var maxCapacity: Int
    @Published var classTypes: [SelectableItem<ClassType>] = []
    @Published var isLoading: Bool = true
    
    init(dependencies: Dependencies, room: Room, delegate: RoomEditViewFlowDelegate? = nil) {
        self.logger = dependencies.logger
        self.roomManager = dependencies.roomManager
        self.classTypeManager = dependencies.classTypeManager
        self.delegate = delegate
        self.room = room
        self.maxCapacity = room.maxCapacity
    }
    
    func onAppear() {
        Task { @MainActor [weak self] in
            guard let self = self else { return }
            self.isLoading = true
            defer { self.isLoading = false }
            
            do {
                try await self.fetchClassTypes()
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
            
            guard maxCapacity > 0 else {
                self.delegate?.onUpdateFailure(message: "Capacity must be a valid positive number")
                return
            }
            
            guard self.classTypes.contains(where: { $0.isSelected }) else {
                self.delegate?.onUpdateFailure(message: "Please select at least one class type")
                return
            }
            
            guard let roomId = self.room.roomId else {
                self.delegate?.onUpdateFailure(message: "Something went wrong")
                return
            }
            
            do {
                let selectedClassTypeIds = self.classTypes.filter { $0.isSelected }.map { $0.item.classTypeId! }
                
                let updatedRoom = Room(
                    roomId: roomId,
                    maxCapacity: maxCapacity,
                    classes: room.classes, // Preserve existing classes
                    classTypes: selectedClassTypeIds
                )
                
                try await self.roomManager.updateRoom(roomId, updatedRoom)
                self.delegate?.onUpdateSuccess()
            } catch let baseError as BaseError {
                delegate?.onUpdateFailure(message: baseError.message)
            } catch {
                delegate?.onUpdateFailure(message: error.localizedDescription)
            }
        }
    }
    
    // MARK: - Private Helpers
    
    private func fetchClassTypes() async throws {
        try await classTypeManager.fetchClassTypes()
        let fetchedClassTypes = classTypeManager.allClassTypes.map { classType in
            SelectableItem(
                item: classType,
                isSelected: room.classTypes.contains(classType.classTypeId ?? -1),
                itemDescription: classType.name
            )
        }
        DispatchQueue.main.async { [weak self] in
            self?.classTypes = fetchedClassTypes
        }
    }
}
