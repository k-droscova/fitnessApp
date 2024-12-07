//
//  InstructorListViewModel.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 07.12.2024.
//

import Foundation
import Combine

protocol InstructorListFlowDelegate: NSObject {
    func onDetailTapped(with instructor: Instructor)
    func onAddTapped()
    func onLoadError()
}

protocol InstructorListViewModeling: BaseClass, ObservableObject {
    var instructors: [Instructor] { get }
    var searchInstructorName: String { get set }
    func onAppear()
    func onDisappear()
    func fetchInstructors()
    func onInstructorTapped(_ instructor: Instructor)
    func onAddButtonTapped()
}

final class InstructorListViewModel: BaseClass, InstructorListViewModeling {
    typealias Dependencies = HasInstructorManager
    
    private let manager: InstructorManaging
    private weak var delegate: InstructorListFlowDelegate?
    private var cancellables = Set<AnyCancellable>()
    
    @Published var instructors: [Instructor] = []
    @Published var searchInstructorName: String = ""
    
    init(dependencies: Dependencies, delegate: InstructorListFlowDelegate? = nil) {
        self.manager = dependencies.instructorManager
        self.delegate = delegate
        
        super.init()
        setupSearch()
    }
    
    func onAppear() {
        if searchInstructorName.isEmpty {
            fetchInstructors()
        }
    }

    func onDisappear() {
        searchInstructorName = ""
    }
    
    func fetchInstructors() {
        Task { [weak self] in
            guard let self = self else { return }
            do {
                try await self.manager.fetchInstructors()
            } catch {
                self.delegate?.onLoadError()
            }
            DispatchQueue.main.async {
                self.instructors = self.manager.allInstructors
            }
        }
    }
    
    func onAddButtonTapped() {
        delegate?.onAddTapped()
    }
    
    func onInstructorTapped(_ instructor: Instructor) {
        delegate?.onDetailTapped(with: instructor)
    }
    
    // MARK: - Private Helpers
    
    private func setupSearch() {
        $searchInstructorName
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
                    // Reset to all instructors if search text is cleared
                    try await self.manager.fetchInstructors()
                    DispatchQueue.main.async {
                        self.instructors = self.manager.allInstructors
                    }
                } else {
                    // Perform the search using API
                    let searchResults = try await self.manager.fetchInstructorsByName(name: nil, surname: nil, input: input)
                    DispatchQueue.main.async {
                        self.instructors = searchResults
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
