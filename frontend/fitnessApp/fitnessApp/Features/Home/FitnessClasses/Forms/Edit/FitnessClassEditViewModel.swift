//
//  FitnessClassEditViewModel.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 06.12.2024.
//

import Foundation

protocol FitnessClassEditViewFlowDelegate: NSObject {
    func onLoadError()
    func onCancelPressed()
    func onUpdateSuccess()
    func onUpdateFailure(message: String)
}

protocol FitnessClassEditViewModeling: BaseClass, ObservableObject {
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
    func onCancelPressed()
    func onSavePressed()
}

final class FitnessClassEditViewModel: BaseClass, FitnessClassEditViewModeling {
    typealias Dependencies = HasFitnessClassManager & HasRoomManager & HasInstructorManager & HasClassTypeManager
    
    private let fitnessClassManager: FitnessClassManaging
    private let roomManager: RoomManaging
    private let instructorManager: InstructorManaging
    private let classTypeManager: ClassTypeManaging
    private weak var delegate: FitnessClassEditViewFlowDelegate?
    private let fitnessClass: FitnessClass
    
    // MARK: - Published Properties
    @Published var date: Date? {
        didSet {
            validateDateAndRefreshOptions()
        }
    }
    @Published var selectedClassType: ClassType? {
        didSet {
            if selectedClassType == nil {
                selectedRoom = nil
                selectedInstructor = nil
                selectedCapacity = nil
                roomOptions = []
                instructorOptions = []
            } else {
                loadRoomsAndInstructors()
            }
        }
    }
    @Published var classTypeOptions: [ClassType] = []
    @Published var selectedRoom: Room? {
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
    var isClassTypeSelectionEnabled: Bool { date != nil }
    var isRoomSelectionEnabled: Bool { selectedClassType != nil && date != nil }
    var isInstructorSelectionEnabled: Bool { selectedClassType != nil && date != nil }
    var isCapacitySelectionEnabled: Bool { selectedRoom != nil }
    
    // MARK: - Initializer
    init(
        dependencies: Dependencies,
        fitnessClass: FitnessClass,
        delegate: FitnessClassEditViewFlowDelegate? = nil
    ) {
        self.fitnessClassManager = dependencies.fitnessClassManager
        self.roomManager = dependencies.roomManager
        self.instructorManager = dependencies.instructorManager
        self.classTypeManager = dependencies.classTypeManager
        self.delegate = delegate
        self.fitnessClass = fitnessClass
    }
    
    // MARK: - Actions
    
    func onAppear() {
        Task { @MainActor [weak self] in
            guard let self = self else { return }
            self.isLoading = true
            self.date = fitnessClass.dateTime
            self.selectedCapacity = fitnessClass.capacity
            defer { self.isLoading = false }
            do {
                async let classTypes: () = self.fetchClassTypeOptions()
                async let roomsAndInstructors: () = self.fetchRoomsAndInstructors()
                
                try await (classTypes, roomsAndInstructors)
                self.selectedClassType = self.classTypeOptions.first(where: { $0.classTypeId == self.fitnessClass.classType })
                self.selectedRoom = self.roomOptions.first(where: { $0.roomId == self.fitnessClass.room })
                self.selectedInstructor = self.instructorOptions.first(where: { $0.instructorId == self.fitnessClass.instructor })
            } catch {
                self.delegate?.onLoadError()
            }
        }
    }
    
    func onCancelPressed() {
        delegate?.onCancelPressed()
    }
    
    func onSavePressed() {
        Task { @MainActor [weak self] in
            guard let self = self else { return }
            self.isLoading = true
            defer { self.isLoading = false }
            guard let date = self.date,
                  let id = self.fitnessClass.fitnessClassId,
                  let classType = self.selectedClassType?.classTypeId,
                  let room = self.selectedRoom?.roomId,
                  let instructor = self.selectedInstructor?.instructorId,
                  let capacity = self.selectedCapacity else {
                self.delegate?.onUpdateFailure(message: "Please complete all fields.")
                return
            }
            
            let updatedFitnessClass = FitnessClass(
                fitnessClassId: fitnessClass.fitnessClassId,
                capacity: capacity,
                dateTime: date,
                instructor: instructor,
                room: room,
                classType: classType,
                trainees: fitnessClass.trainees
            )
            
            do {
                try await self.fitnessClassManager.updateFitnessClass(id, updatedFitnessClass)
                self.delegate?.onUpdateSuccess()
            } catch {
                self.delegate?.onUpdateFailure(message: "Failed to update fitness class.")
            }
        }
    }
    
    // MARK: - Helpers
    private func validateDateAndRefreshOptions() {
        guard let date = date, Calendar.current.isDateInFuture(date) else {
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
    
    private func fetchClassTypeOptions() async throws {
        try await classTypeManager.fetchClassTypes()
        DispatchQueue.main.async { [weak self] in
            self?.classTypeOptions = self?.classTypeManager.allClassTypes ?? []
        }
    }
    
    private func loadRoomsAndInstructors() {
        Task { @MainActor [weak self] in
            do {
                try await self?.fetchRoomsAndInstructors()
            } catch {
                self?.delegate?.onLoadError()
            }
        }
    }
    
    private func fetchRoomsAndInstructors() async throws {
        guard let classType = selectedClassType, let date = date else { return }

        // Ensure the room and instructor assigned to the fitness class are included in the options
        let (dateString, timeString) = Date.Backend.split(dateTime: date)
        async let fetchRoomsTask = fetchAvailableRooms(classType: classType, dateString: dateString, timeString: timeString)
        async let fetchInstructorsTask = fetchAvailableInstructors(classType: classType, dateString: dateString, timeString: timeString)
        
        var rooms: [Room] = []
        var instructors: [Instructor] = []

        rooms = try await fetchRoomsTask
        instructors = try await fetchInstructorsTask
        
        // Include currently selected room and instructor if valid
        if classType.classTypeId == fitnessClass.classType && Calendar.current.isDate(fitnessClass.dateTime, equalTo: date, toGranularity: .minute) {
            let currentRoomId = fitnessClass.room
            if let currentRoom = try? await roomManager.fetchRoomById(currentRoomId), !rooms.contains(where: { $0.roomId == currentRoomId }) {
                rooms.append(currentRoom)
            }
            let currentInstructorId = fitnessClass.instructor
            if let currentInstructor = try? await instructorManager.fetchInstructorById(currentInstructorId), !instructors.contains(where: { $0.instructorId == currentInstructorId }) {
                instructors.append(currentInstructor)
            }
        }

        DispatchQueue.main.async { [weak self] in
            self?.roomOptions = rooms
            self?.instructorOptions = instructors
        }
    }
    
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
