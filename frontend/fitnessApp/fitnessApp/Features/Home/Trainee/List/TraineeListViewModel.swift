//
//  TraineeListViewModel.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 07.12.2024.
//

import Foundation
import Combine

protocol TraineeListFlowDelegate: NSObject {
    func onDetailTapped(with trainee: Trainee)
    func onAddTapped()
    func onLoadError()
}

protocol TraineeListViewModeling: BaseClass, ObservableObject {
    var trainees: [Trainee] { get }
    var searchTraineeName: String { get set }
    func onAppear()
    func onDisappear()
    func fetchTrainees()
    func onTraineeTapped(_ trainee: Trainee)
    func onAddButtonTapped()
}

final class TraineeListViewModel: BaseClass, TraineeListViewModeling {
    typealias Dependencies = HasTraineeManager
    
    private let manager: TraineeManaging
    private weak var delegate: TraineeListFlowDelegate?
    private var cancellables = Set<AnyCancellable>()
    
    @Published var trainees: [Trainee] = []
    @Published var searchTraineeName: String = ""
    
    init(dependencies: Dependencies, delegate: TraineeListFlowDelegate? = nil) {
        self.manager = dependencies.traineeManager
        self.delegate = delegate
        
        super.init()
        setupSearch()
    }
    
    func onAppear() {
        if searchTraineeName.isEmpty {
            fetchTrainees()
        }
    }

    func onDisappear() {
        searchTraineeName = ""
    }
    
    func fetchTrainees() {
        Task { [weak self] in
            guard let self = self else { return }
            do {
                try await self.manager.fetchAllTrainees()
            } catch {
                self.delegate?.onLoadError()
            }
            DispatchQueue.main.async {
                self.trainees = self.manager.allTrainees
            }
        }
    }
    
    func onAddButtonTapped() {
        delegate?.onAddTapped()
    }
    
    func onTraineeTapped(_ trainee: Trainee) {
        delegate?.onDetailTapped(with: trainee)
    }
    
    // MARK: - Private Helpers
    
    private func setupSearch() {
        $searchTraineeName
            .debounce(for: .milliseconds(300), scheduler: DispatchQueue.main) // Adjust debounce delay as needed
            .removeDuplicates()
            .sink { [weak self] searchText in
                self?.performSearch(with: searchText)
            }
            .store(in: &cancellables)
    }
    
    private func performSearch(with input: String) {
        Task { [weak self] in
            guard let self = self else { return }
            do {
                if input.isEmpty {
                    // Reset to all trainees if search text is cleared
                    try await self.manager.fetchAllTrainees()
                    DispatchQueue.main.async {
                        self.trainees = self.manager.allTrainees
                    }
                } else {
                    // Perform the search using API
                    let searchResults = try await self.manager.fetchTraineesByName(input)
                    DispatchQueue.main.async {
                        self.trainees = searchResults
                    }
                }
            } catch {
                DispatchQueue.main.async {
                    self.delegate?.onLoadError()
                }
            }
        }
    }
}
