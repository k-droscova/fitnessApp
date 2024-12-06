//
//  FitnessClassListViewModel.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 06.12.2024.
//

import Foundation
import Combine

protocol FitnessClassListFlowDelegate: NSObject {
    func onDetailTapped(with fitnessClass: FitnessClass)
    func onAddTapped()
    func onLoadError()
}

protocol FitnessClassListViewModeling: BaseClass, ObservableObject {
    var fitnessClasses: [FitnessClass] { get }
    var filteredClasses: [FitnessClass] { get }
    
    var dateFromFilter: Date? { get set }
    var dateToFilter: Date? { get set }
    var timeFromFilter: Date? { get set }
    var timeToFilter: Date? { get set }
    
    var errorMessage: String? { get }
    var areFiltersExpanded: Bool { get set }
    
    func onAppear()
    func fetchFitnessClasses()
    func onFitnessClassTapped(_ fitnessClass: FitnessClass)
    func onAddButtonTapped()
    func clearFilters()
}

final class FitnessClassListViewModel: BaseClass, FitnessClassListViewModeling {
    typealias Dependencies = HasFitnessClassManager & HasClassTypeManager
    
    private let manager: FitnessClassManaging
    private let classTypeManager: ClassTypeManaging
    private weak var delegate: FitnessClassListFlowDelegate?
    
    // Published properties
    @Published var fitnessClasses: [FitnessClass] = []
    @Published var filteredClasses: [FitnessClass] = []
    
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
    @Published private(set) var classTypeMap: [Int: String] = [:]
    
    private var cancellables = Set<AnyCancellable>()
    
    init(dependencies: Dependencies, delegate: FitnessClassListFlowDelegate? = nil) {
        self.manager = dependencies.fitnessClassManager
        self.classTypeManager = dependencies.classTypeManager
        self.delegate = delegate
        
        super.init()
        self.setupFilterBinding()
    }
    
    func onAppear() {
        fetchFitnessClasses()
    }
    
    func fetchFitnessClasses() {
        Task { @MainActor [weak self] in
            guard let self = self else { return }
            do {
                try await self.manager.fetchFitnessClasses()
                try await self.classTypeManager.fetchClassTypes()
                DispatchQueue.main.async {
                    self.fitnessClasses = self.manager.allFitnessClasses
                    self.classTypeMap = Dictionary(
                        uniqueKeysWithValues: self.classTypeManager.allClassTypes.map { ($0.classTypeId ?? 0, $0.name) }
                    )
                    self.applyFilters() // Ensure filtered list is updated
                }
            } catch {
                self.delegate?.onLoadError()
            }
        }
    }
    
    func onAddButtonTapped() {
        delegate?.onAddTapped()
    }
    
    func onFitnessClassTapped(_ fitnessClass: FitnessClass) {
        delegate?.onDetailTapped(with: fitnessClass)
    }
    
    func clearFilters() {
        dateFromFilter = nil
        dateToFilter = nil
        timeFromFilter = nil
        timeToFilter = nil
        errorMessage = nil
    }
    
    func classTypeNameForFitnessClass(_ fitnessClass: FitnessClass) -> String {
        classTypeMap[fitnessClass.classType] ?? "Unknown"
    }
    
    // MARK: - Private Helpers
    
    private func setupFilterBinding() {
        $dateFromFilter
            .combineLatest($dateToFilter, $timeFromFilter, $timeToFilter)
            .sink { [weak self] _, _, _, _ in
                self?.applyFilters()
            }
            .store(in: &cancellables)
    }
    
    private func applyFilters() {
        // Clear the error message
        errorMessage = nil
        
        // Validate filter logic
        if let dateFrom = dateFromFilter, let dateTo = dateToFilter, dateFrom > dateTo {
            errorMessage = "Date From cannot be later than Date To."
            filteredClasses = []
            return
        }
        
        if let timeFrom = timeFromFilter, let timeTo = timeToFilter, timeFrom > timeTo {
            errorMessage = "Time From cannot be later than Time To."
            filteredClasses = []
            return
        }
        
        // Apply the filters
        filteredClasses = fitnessClasses.filter { filterFitnessClass($0) }
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
