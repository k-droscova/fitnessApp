//
//  TraineeRegistrationViewModel.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 07.12.2024.
//

import Foundation
import Combine

protocol TraineeRegistrationViewFlowDelegate: NSObject {
    func onTraineeRegistrationSuccess()
    func onTraineeRegistrationFailure(message: String)
    func onRegistrationDismiss()
}

protocol TraineeRegistrationViewModeling: BaseClass, ObservableObject {
    var isLoading: Bool { get }
    var fitnessClasses: [FitnessClass] { get }
    var filteredClasses: [FitnessClass] { get }
    var selectedClasses: [Int: Bool] { get set }
    var isRegisteredButtonDisabled: Bool { get }

    var dateFromFilter: Date? { get set }
    var dateToFilter: Date? { get set }
    var timeFromFilter: Date? { get set }
    var timeToFilter: Date? { get set }
    var errorMessage: String? { get }
    var areFiltersExpanded: Bool { get set }

    func onAppear()
    func onDisappear()
    func registerForClasses()
    func classTypeName(for fitnessClass: FitnessClass) -> String
    func isClassFull(for fitnessClass: FitnessClass) -> Bool
    func isRegisteredForClass(for fitnessClass: FitnessClass) -> Bool
    func clearFilters()
}

final class TraineeRegistrationViewModel: BaseClass, TraineeRegistrationViewModeling {
    typealias Dependencies = HasLoggerService & HasFitnessClassManager & HasClassTypeManager

    private let logger: LoggerServicing
    private let fitnessClassManager: FitnessClassManaging
    private let classTypeManager: ClassTypeManaging
    private weak var delegate: TraineeRegistrationViewFlowDelegate?
    private let trainee: Trainee

    var isRegisteredButtonDisabled: Bool { selectedClasses.values.filter { $0 }.isEmpty }
    @Published var fitnessClasses: [FitnessClass] = []
    @Published var filteredClasses: [FitnessClass] = []
    @Published var classTypeMap: [Int: String] = [:]
    @Published var selectedClasses: [Int: Bool] = [:]
    @Published var isLoading: Bool = true

    @Published var dateFromFilter: Date? = nil {
        didSet { applyFilters() }
    }
    @Published var dateToFilter: Date? = nil {
        didSet { applyFilters() }
    }
    @Published var timeFromFilter: Date? = nil {
        didSet { applyFilters() }
    }
    @Published var timeToFilter: Date? = nil {
        didSet { applyFilters() }
    }
    @Published var areFiltersExpanded: Bool = false
    @Published private(set) var errorMessage: String? = nil

    private var cancellables = Set<AnyCancellable>()

    init(dependencies: Dependencies, trainee: Trainee, delegate: TraineeRegistrationViewFlowDelegate? = nil) {
        self.logger = dependencies.logger
        self.fitnessClassManager = dependencies.fitnessClassManager
        self.classTypeManager = dependencies.classTypeManager
        self.delegate = delegate
        self.trainee = trainee

        super.init()
        self.setupFilterBinding()
    }

    func onAppear() {
        Task { @MainActor [weak self] in
            guard let self = self else { return }
            self.isLoading = true
            defer { self.isLoading = false }

            do {
                try await self.loadData()
            } catch {
                self.delegate?.onTraineeRegistrationFailure(message: "Failed to load fitness classes.")
            }
        }
    }

    func onDisappear() {
        cancellables.removeAll()
        delegate?.onRegistrationDismiss()
    }

    func registerForClasses() {
        Task { @MainActor [weak self] in
            guard let self = self else { return }
            self.isLoading = true
            defer { self.isLoading = false }

            let selectedIds = self.selectedClasses
                .filter { $0.value }
                .map { $0.key }

            guard !selectedIds.isEmpty else {
                self.delegate?.onTraineeRegistrationFailure(message: "No classes selected.")
                return
            }

            guard let id = self.trainee.traineeId else {
                self.delegate?.onTraineeRegistrationFailure(message: "Something went wrong.")
                return
            }

            do {
                for classId in selectedIds {
                    try await self.fitnessClassManager.addTraineeToFitnessClass(classId, id)
                }
                self.delegate?.onTraineeRegistrationSuccess()
            } catch {
                self.delegate?.onTraineeRegistrationFailure(message: "Failed to register for classes.")
            }
        }
    }

    func classTypeName(for fitnessClass: FitnessClass) -> String {
        classTypeMap[fitnessClass.classType] ?? "Unknown"
    }

    func isClassFull(for fitnessClass: FitnessClass) -> Bool {
        fitnessClass.trainees.count >= fitnessClass.capacity
    }

    func isRegisteredForClass(for fitnessClass: FitnessClass) -> Bool {
        guard let traineeId = self.trainee.traineeId else { return false }
        return fitnessClass.trainees.contains(traineeId)
    }

    func clearFilters() {
        dateFromFilter = nil
        dateToFilter = nil
        timeFromFilter = nil
        timeToFilter = nil
        errorMessage = nil
    }

    // MARK: - Private Helpers

    private func loadData() async throws {
        try await withThrowingTaskGroup(of: Void.self) { group in
            group.addTask {
                try await self.fitnessClassManager.fetchFitnessClasses()
            }
            group.addTask {
                try await self.classTypeManager.fetchClassTypes()
            }
            try await group.waitForAll()
        }

        let futureClasses = self.fitnessClassManager.allFitnessClasses
            .filter { Calendar.current.isDateTimeInFuture($0.dateTime) }

        await MainActor.run {
            self.fitnessClasses = futureClasses
            self.classTypeMap = Dictionary(
                uniqueKeysWithValues: self.classTypeManager.allClassTypes.map { ($0.classTypeId ?? 0, $0.name) }
            )
            
            self.selectedClasses = Dictionary(
                uniqueKeysWithValues: futureClasses.map { ($0.fitnessClassId ?? 0, false) }
            )
        }
        self.applyFilters()
    }

    private func setupFilterBinding() {
        $dateFromFilter
            .combineLatest($dateToFilter, $timeFromFilter, $timeToFilter)
            .sink { [weak self] _, _, _, _ in
                self?.applyFilters()
            }
            .store(in: &cancellables)
    }

    private func applyFilters() {
        Task { @MainActor [weak self] in
            guard let self = self else { return }
            // Clear the error message
            self.errorMessage = nil
            
            // Validate filter logic
            if let dateFrom = self.dateFromFilter, let dateTo = self.dateToFilter, dateFrom > dateTo {
                self.errorMessage = "Date From cannot be later than Date To."
                self.filteredClasses = []
                return
            }
            
            if let timeFrom = self.timeFromFilter, let timeTo = self.timeToFilter, timeFrom > timeTo {
                self.errorMessage = "Time From cannot be later than Time To."
                self.filteredClasses = []
                return
            }
            
            // Apply the filters
            self.filteredClasses = self.fitnessClasses.filter { self.filterFitnessClass($0) }
        }
    }

    private func filterFitnessClass(_ fitnessClass: FitnessClass) -> Bool {
        let calendar = Calendar.current

        // Filter by Date From
        if let dateFromFilter = dateFromFilter {
            if calendar.startOfDay(for: fitnessClass.dateTime) < calendar.startOfDay(for: dateFromFilter) {
                return false
            }
        }

        // Filter by Date To
        if let dateToFilter = dateToFilter {
            if calendar.startOfDay(for: fitnessClass.dateTime) > calendar.startOfDay(for: dateToFilter) {
                return false
            }
        }

        // Filter by Time From
        if let timeFromFilter = timeFromFilter {
            let fitnessClassTime = calendar.dateComponents([.hour, .minute], from: fitnessClass.dateTime)
            let filterTime = calendar.dateComponents([.hour, .minute], from: timeFromFilter)
            if fitnessClassTime.hour! < filterTime.hour! ||
                (fitnessClassTime.hour! == filterTime.hour! && fitnessClassTime.minute! < filterTime.minute!) {
                return false
            }
        }

        // Filter by Time To
        if let timeToFilter = timeToFilter {
            let fitnessClassTime = calendar.dateComponents([.hour, .minute], from: fitnessClass.dateTime)
            let filterTime = calendar.dateComponents([.hour, .minute], from: timeToFilter)
            if fitnessClassTime.hour! > filterTime.hour! ||
                (fitnessClassTime.hour! == filterTime.hour! && fitnessClassTime.minute! > filterTime.minute!) {
                return false
            }
        }

        return true
    }
}
