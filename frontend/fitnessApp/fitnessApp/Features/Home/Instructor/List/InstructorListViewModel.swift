//
//  InstructorListViewModel.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 07.12.2024.
//

import Foundation

protocol InstructorListFlowDelegate: NSObject {
    func onDetailTapped(with instructor: Instructor)
    func onAddTapped()
    func onLoadError()
}

protocol InstructorListViewModeling: BaseClass, ObservableObject {
    var instructors: [Instructor] { get }
    func onAppear()
    func fetchInstructors()
    func onInstructorTapped(_ instructor: Instructor)
    func onAddButtonTapped()
}

final class InstructorListViewModel: BaseClass, InstructorListViewModeling {
    typealias Dependencies = HasInstructorManager

    private let manager: InstructorManaging
    private weak var delegate: InstructorListFlowDelegate?
    
    @Published var instructors: [Instructor] = []

    init(dependencies: Dependencies, delegate: InstructorListFlowDelegate? = nil) {
        self.manager = dependencies.instructorManager
        self.delegate = delegate
    }
    
    func onAppear() {
        fetchInstructors()
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
}
