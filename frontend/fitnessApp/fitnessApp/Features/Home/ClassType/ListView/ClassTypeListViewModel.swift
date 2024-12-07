//
//  ClassTypeViewModel.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 05.12.2024.
//

import Foundation
import Combine

protocol ClassTypeListFlowDelegate: NSObject {
    func onDetailTapped(with classType: ClassType)
    func onAddTapped()
    func onLoadError()
}

protocol ClassTypeListViewModeling: BaseClass, ObservableObject {
    var classTypes: [ClassType] { get }
    var searchClassTypeName: String { get set }
    func onAppear()
    func onDisappear()
    func fetchClassTypes()
    func onClassTypeTapped(_ classType: ClassType)
    func onAddButtonTapped()
}

final class ClassTypeListViewModel: BaseClass, ClassTypeListViewModeling {
    typealias Dependencies = HasClassTypeManager

    private let manager: ClassTypeManaging
    private weak var delegate: ClassTypeListFlowDelegate?
    private var cancellables = Set<AnyCancellable>()

    @Published var classTypes: [ClassType] = []
    @Published var searchClassTypeName: String = ""

    init(dependencies: Dependencies, delegate: ClassTypeListFlowDelegate? = nil) {
        self.manager = dependencies.classTypeManager
        self.delegate = delegate

        super.init()
        setupSearch()
    }

    func onAppear() {
        if searchClassTypeName.isEmpty {
            fetchClassTypes()
        }
    }

    func onDisappear() {
        searchClassTypeName = ""
    }

    func fetchClassTypes() {
        Task { [weak self] in
            guard let self = self else { return }
            do {
                try await self.manager.fetchClassTypes()
            } catch {
                self.delegate?.onLoadError()
            }
            DispatchQueue.main.async {
                self.classTypes = self.manager.allClassTypes
            }
        }
    }

    func onAddButtonTapped() {
        delegate?.onAddTapped()
    }

    func onClassTypeTapped(_ classType: ClassType) {
        delegate?.onDetailTapped(with: classType)
    }

    // MARK: - Private Helpers

    private func setupSearch() {
        $searchClassTypeName
            .debounce(for: .milliseconds(300), scheduler: DispatchQueue.main)
            .removeDuplicates()
            .sink { [weak self] searchText in
                self?.performSearch(with: searchText)
            }
            .store(in: &cancellables)
    }

    private func performSearch(with name: String) {
        Task { [weak self] in
            guard let self = self else { return }
            do {
                if name.isEmpty {
                    // Reset to all class types if search text is cleared
                    try await self.manager.fetchClassTypes()
                    DispatchQueue.main.async {
                        self.classTypes = self.manager.allClassTypes
                    }
                } else {
                    // Perform the search using API
                    let searchResults = try await self.manager.fetchClassTypeByName(name)
                    DispatchQueue.main.async {
                        self.classTypes = searchResults
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
