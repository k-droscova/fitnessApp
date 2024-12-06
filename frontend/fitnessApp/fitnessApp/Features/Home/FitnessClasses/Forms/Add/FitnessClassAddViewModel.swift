//
//  FitnessClassAddViewModel.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 06.12.2024.
//

import Foundation

protocol FitnessClassAddViewFlowDelegate: NSObject {
    func onLoadError()
    func onBackPressed()
    func onSaveSuccess()
    func onSaveFailure(message: String)
}

protocol FitnessClassAddViewModeling: BaseClass, ObservableObject {
    // class type selection
    var isClassTypeSelectionEnabled: Bool { get }
    var selectedClassType: ClassType? { get set }
    var classTypeOptions: [ClassType] { get }
    
    // room selection
    var isRoomSelectionEnabled: Bool { get }
    var selectedRoom: Room? { get set }
    var roomOptions: [Room] { get }
    
    // instructor selection
    var isInstructorSelectionEnabled: Bool { get }
    var selectedInstructor: Instructor? { get set }
    var instructorOptions: [Instructor] { get }
    
    // date
    var date: Date? { get set }
    
    // Capacity Selection
    var isCapacitySelectionEnabled: Bool { get }
    var maxCapacity: Int { get }
    var selectedCapacity: Int? { get set }
    
    var isLoading: Bool { get }
    func onAppear()
    func onBackPressed()
    func onSavePressed()
}

final class FitnessClassAddViewModel: BaseClass, FitnessClassAddViewModeling {
    typealias Dependencies = HasFitnessClassManager & HasRoomManager & HasInstructorManager & HasClassTypeManager
    
    private let fitnessClassManager: FitnessClassManaging
    private let roomManager: RoomManaging
    private let instructorManager: InstructorManaging
    private let classTypeManager: ClassTypeManaging
    private weak var delegate: FitnessClassAddViewFlowDelegate?
    
    // MARK: - Published Properties
    @Published var date: Date? = nil {
        didSet {
            validateDateAndRefreshOptions()
        }
    }
    @Published var selectedClassType: ClassType? = nil {
        didSet {
            selectedRoom = nil
            selectedInstructor = nil
            selectedCapacity = nil
            if selectedClassType == nil {
                roomOptions = []
                instructorOptions = []
            } else {
                Task { await fetchRoomsAndInstructors() }
            }
        }
    }
    @Published var classTypeOptions: [ClassType] = []
    @Published var selectedRoom: Room? = nil {
        didSet {
            if let room = selectedRoom {
                maxCapacity = room.maxCapacity
                selectedCapacity = min(maxCapacity, selectedCapacity ?? 0)
            } else {
                maxCapacity = 0
                selectedCapacity = nil
            }
        }
    }
    @Published var roomOptions: [Room] = []
    @Published var selectedInstructor: Instructor? = nil
    @Published var instructorOptions: [Instructor] = []
    @Published var selectedCapacity: Int? = nil
    @Published var maxCapacity: Int = 0
    
    @Published var isLoading: Bool = false
    
    // MARK: - Computed Properties
    var isDateSelectionEnabled: Bool {
        true
    }
    
    var isClassTypeSelectionEnabled: Bool {
        date != nil
    }
    
    var isRoomSelectionEnabled: Bool {
        selectedClassType != nil && date != nil
    }
    
    var isInstructorSelectionEnabled: Bool {
        selectedClassType != nil && date != nil
    }
    
    var isCapacitySelectionEnabled: Bool {
        selectedRoom != nil
    }
    
    init(
        dependencies: Dependencies,
        delegate: FitnessClassAddViewFlowDelegate? = nil
    ) {
        self.fitnessClassManager = dependencies.fitnessClassManager
        self.roomManager = dependencies.roomManager
        self.instructorManager = dependencies.instructorManager
        self.classTypeManager = dependencies.classTypeManager
        self.delegate = delegate
    }
    
    // MARK: - Actions
    func onAppear() {
        Task {
            await loadClassTypeOptions()
        }
    }
    
    func onBackPressed() {
        delegate?.onBackPressed()
    }
    
    func onSavePressed() {
        Task { @MainActor [weak self] in
            guard let self = self else { return }
            self.isLoading = true
            defer { self.isLoading = false }
            guard let date = self.date,
                  let classType = self.selectedClassType?.classTypeId,
                  let room = self.selectedRoom?.roomId,
                  let instructor = self.selectedInstructor?.instructorId,
                  let capacity = self.selectedCapacity else {
                self.delegate?.onSaveFailure(message: "Please complete all fields.")
                return
            }
            
            let newFitnessClass = FitnessClass(
                fitnessClassId: nil,
                capacity: capacity,
                dateTime: date,
                instructor: instructor,
                room: room,
                classType: classType,
                trainees: []
            )
            
            do {
                try await self.fitnessClassManager.createFitnessClass(newFitnessClass)
                self.delegate?.onSaveSuccess()
            } catch {
                self.delegate?.onSaveFailure(message: "Failed to save fitness class.")
            }
        }
    }
    
    // MARK: - Helpers
    private func validateDateAndRefreshOptions() {
        guard let date = date, Calendar.current.isDateInFuture(date) else {
            delegate?.onSaveFailure(message: "Date and time must be in the future.")
            clearSelections()
            return
        }
        
        // Clear dependent selections
        selectedClassType = nil
        roomOptions = []
        instructorOptions = []
        selectedRoom = nil
        selectedInstructor = nil
        selectedCapacity = nil
    }
    
    private func clearSelections() {
        selectedClassType = nil
        selectedRoom = nil
        selectedInstructor = nil
        selectedCapacity = nil
        maxCapacity = 0
        roomOptions = []
        instructorOptions = []
    }
    
    private func loadClassTypeOptions() async {
        do {
            try await classTypeManager.fetchClassTypes()
            DispatchQueue.main.async { [weak self] in
                self?.classTypeOptions = self?.classTypeManager.allClassTypes ?? []
            }
        } catch {
            delegate?.onLoadError()
        }
    }
    
    private func fetchRoomsAndInstructors() async {
        guard let classType = selectedClassType, let date = date else { return }
        
        let (dateString, timeString) = Date.Backend.split(dateTime: date)

        async let fetchRoomsTask = fetchAvailableRooms(classType: classType, dateString: dateString, timeString: timeString)
        async let fetchInstructorsTask = fetchAvailableInstructors(classType: classType, dateString: dateString, timeString: timeString)

        do {
            let (rooms, instructors) = try await (fetchRoomsTask, fetchInstructorsTask)
            
            DispatchQueue.main.async { [weak self] in
                self?.roomOptions = rooms
                self?.instructorOptions = instructors
            }
        } catch {
            delegate?.onLoadError()
        }
    }

    // Separate async calls for rooms and instructors
    private func fetchAvailableRooms(classType: ClassType, dateString: String, timeString: String) async throws -> [Room] {
        let roomIds = try await roomManager.findAvailableRooms(
            classTypeId: classType.classTypeId,
            date: dateString,
            time: timeString
        )
        let fetchedRooms = try await withThrowingTaskGroup(of: Room.self) { group -> [Room] in
            for roomId in roomIds {
                group.addTask { try await self.roomManager.fetchRoomById(roomId) }
            }
            return try await group.reduce(into: [Room]()) { $0.append($1) }
        }
        return fetchedRooms
    }

    private func fetchAvailableInstructors(classType: ClassType, dateString: String, timeString: String) async throws -> [Instructor] {
        let instructorIds = try await instructorManager.findAvailableInstructors(
            classTypeId: classType.classTypeId,
            date: dateString,
            time: timeString
        )
        let fetchedInstructors = try await withThrowingTaskGroup(of: Instructor.self) { group -> [Instructor] in
            for instructorId in instructorIds {
                group.addTask { try await self.instructorManager.fetchInstructorById(instructorId) }
            }
            return try await group.reduce(into: [Instructor]()) { $0.append($1) }
        }
        return fetchedInstructors
    }
}

