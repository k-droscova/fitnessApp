//
//  FitnessClassEditViewModel.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 06.12.2024.
//

import Foundation

protocol FitnessClassEditViewFlowDelegate: NSObject {
    func onLoadError()
    func onEditViewDismissed()
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
    var isSaveDisabled: Bool { get }
    func onAppear()
    func onDisappear()
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
    private var isInitializingData: Bool = true
    
    // MARK: - Published Properties
    @Published var date: Date? {
        didSet {
            guard !isInitializingData else { return }
            validateDateAndRefreshOptions()
            isSaveDisabled = isSaveButtonDisabled()
        }
    }
    @Published var selectedClassType: ClassType? {
        didSet {
            guard !isInitializingData else { return }
            selectedRoom = nil
            selectedInstructor = nil
            selectedCapacity = nil
            if selectedClassType == nil {
                roomOptions = []
                instructorOptions = []
            } else {
                loadRoomsAndInstructors()
            }
            isSaveDisabled = isSaveButtonDisabled()
        }
    }
    @Published var classTypeOptions: [ClassType] = []
    @Published var selectedRoom: Room? {
        didSet {
            guard !isInitializingData else { return }
            if let room = selectedRoom {
                maxCapacity = room.maxCapacity
                selectedCapacity = min(maxCapacity, selectedCapacity ?? 0)
            } else {
                maxCapacity = 0
                selectedCapacity = nil
            }
            isSaveDisabled = isSaveButtonDisabled()
        }
    }
    @Published var roomOptions: [Room] = []
    @Published var selectedInstructor: Instructor? = nil {
        didSet {
            guard !isInitializingData else { return }
            isSaveDisabled = isSaveButtonDisabled()
        }
    }
    @Published var instructorOptions: [Instructor] = []
    @Published var selectedCapacity: Int? = nil
    @Published var maxCapacity: Int = 0
    @Published var isLoading: Bool = false
    @Published var isSaveDisabled: Bool = true
    
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
            defer { self.isLoading = false }
            defer { self.isInitializingData = false }
            do {
                try await self.populateData()
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
    @MainActor
    private func populateData() async throws {
        // Populate date and capacity
        self.date = fitnessClass.dateTime
        self.selectedCapacity = fitnessClass.capacity
        
        // Fetch class type options
        try await fetchClassTypesForInitialData()
        
        // Fetch rooms and instructors based on the fitness class's initial data
        try await fetchRoomsAndInstructorsForInitialData()
    }
    
    @MainActor
    private func fetchClassTypesForInitialData() async throws {
        try await fetchClassTypeOptions()
        self.selectedClassType = self.classTypeOptions.first(where: { $0.classTypeId == fitnessClass.classType })
    }
    
    @MainActor
    private func fetchRoomsAndInstructorsForInitialData() async throws {
        let (dateString, timeString) = Date.Backend.split(dateTime: fitnessClass.dateTime)
        
        async let roomsTask = fetchAvailableRooms(
            classTypeId: fitnessClass.classType,
            dateString: dateString,
            timeString: timeString
        )
        async let instructorsTask = fetchAvailableInstructors(
            classTypeId: fitnessClass.classType,
            dateString: dateString,
            timeString: timeString
        )
        
        let rooms = try await roomsTask
        let instructors = try await instructorsTask
        
        DispatchQueue.main.async { [weak self] in
            self?.roomOptions = rooms
            self?.instructorOptions = instructors
            self?.selectedRoom = rooms.first(where: { $0.roomId == self?.fitnessClass.room })
            self?.selectedInstructor = instructors.first(where: { $0.instructorId == self?.fitnessClass.instructor })
        }
    }
    
    private func fetchRoomsAndInstructors() async throws -> (rooms: [Room], instructors: [Instructor]) {
        guard let classTypeId = selectedClassType?.classTypeId, let date = date else {
            return ([], [])
        }
        
        let (dateString, timeString) = Date.Backend.split(dateTime: date)
        
        async let roomsTask = fetchAvailableRooms(
            classTypeId: classTypeId,
            dateString: dateString,
            timeString: timeString
        )
        async let instructorsTask = fetchAvailableInstructors(
            classTypeId: classTypeId,
            dateString: dateString,
            timeString: timeString
        )
        
        let rooms = try await roomsTask
        let instructors = try await instructorsTask
        
        return (rooms, instructors)
    }
    
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
                guard let (rooms, instructors) = try await self?.fetchRoomsAndInstructors() else { self?.delegate?.onLoadError(); return }
                self?.roomOptions = rooms
                self?.instructorOptions = instructors
            } catch {
                self?.delegate?.onLoadError()
            }
        }
    }
    
    private func fetchAvailableRooms(classTypeId: Int, dateString: String, timeString: String) async throws -> [Room] {
        let roomIds = try await roomManager.fetchAvailableRooms(
            classTypeId: classTypeId,
            date: dateString,
            time: timeString
        )
        var fetchedRooms = try await withThrowingTaskGroup(of: Room.self) { group -> [Room] in
            for roomId in roomIds {
                group.addTask { try await self.roomManager.fetchRoomById(roomId) }
            }
            return try await group.reduce(into: [Room]()) { $0.append($1) }
        }
        
        guard let date = date else { return fetchedRooms }
        
        if classTypeId == fitnessClass.classType &&
            Calendar.current.isDateEqualToMinute(date, fitnessClass.dateTime) {
            let currentRoomId = fitnessClass.room
            if let currentRoom = try? await roomManager.fetchRoomById(currentRoomId),
               !fetchedRooms.contains(where: { $0.roomId == currentRoomId }) {
                fetchedRooms.append(currentRoom)
            }
        }
        
        return fetchedRooms
    }
    
    private func fetchAvailableInstructors(classTypeId: Int, dateString: String, timeString: String) async throws -> [Instructor] {
        let instructorIds = try await instructorManager.fetchAvailableInstructors(
            classTypeId: classTypeId,
            date: dateString,
            time: timeString
        )
        var fetchedInstructors = try await withThrowingTaskGroup(of: Instructor.self) { group -> [Instructor] in
            for instructorId in instructorIds {
                group.addTask { try await self.instructorManager.fetchInstructorById(instructorId) }
            }
            return try await group.reduce(into: [Instructor]()) { $0.append($1) }
        }
        
        guard let date = date else { return fetchedInstructors }
        
        if classTypeId == fitnessClass.classType &&
            Calendar.current.isDateEqualToMinute(date, fitnessClass.dateTime) {
            let currentInstructorId = fitnessClass.instructor
            if let currentInstructor = try? await instructorManager.fetchInstructorById(currentInstructorId),
               !fetchedInstructors.contains(where: { $0.instructorId == currentInstructorId }) {
                fetchedInstructors.append(currentInstructor)
            }
        }
        
        return fetchedInstructors
    }
    
    private func isSaveButtonDisabled() -> Bool {
        guard let date = date else { return true }
        
        return Calendar.current.isDateEqualToMinute(date, fitnessClass.dateTime) &&
        selectedClassType?.classTypeId == fitnessClass.classType &&
        selectedRoom?.roomId == fitnessClass.room &&
        selectedInstructor?.instructorId == fitnessClass.instructor &&
        selectedCapacity == fitnessClass.capacity
    }
}
