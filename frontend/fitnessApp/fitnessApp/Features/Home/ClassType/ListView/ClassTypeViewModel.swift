//
//  ClassTypeViewModel.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 05.12.2024.
//

import Foundation

protocol ClassTypeListFlowDelegate: NSObject {
    func onDetailTapped(with classType: ClassType)
    func onAddTapped()
    func onLoadError()
}

protocol ClassTypeListViewModeling: BaseClass, ObservableObject {
    var classTypes: [ClassType] { get }
    func onAppear()
    func fetchClassTypes()
    func onClassTypeTapped(_ classType: ClassType)
    func onAddButtonTapped()
}

final class ClassTypeListViewModel: BaseClass, ClassTypeListViewModeling {
    typealias Dependencies = HasClassTypeManager

    private let manager: ClassTypeManaging
    private weak var delegate: ClassTypeListFlowDelegate?
    
    @Published var classTypes: [ClassType] = []


    init(dependencies: Dependencies, delegate: ClassTypeListFlowDelegate? = nil) {
        self.manager = dependencies.classTypeManager
        self.delegate = delegate
    }
    
    func onAppear() {
        fetchClassTypes()
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
}
