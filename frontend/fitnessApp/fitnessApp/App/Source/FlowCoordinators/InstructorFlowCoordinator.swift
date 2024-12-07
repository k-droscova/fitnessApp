//
//  InstructorFlowCoordinator.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 07.12.2024.
//

import Foundation
import UIKit
import ACKategories

protocol InstructorFlowCoordinatorDelegate: NSObject {}

final class InstructorFlowCoordinator: Base.FlowCoordinatorNoDeepLink, BaseFlowCoordinator {
    private weak var delegate: InstructorFlowCoordinatorDelegate?
    private var listViewModel: InstructorListViewModel?

    init(delegate: InstructorFlowCoordinatorDelegate? = nil) {
        self.delegate = delegate
        super.init()
    }

    override func start() -> UIViewController {
        super.start()
        
        let vm = InstructorListViewModel(
            dependencies: appDependencies,
            delegate: self
        )
        self.listViewModel = vm
        let vc = InstructorListView(viewModel: vm).hosting()
        let navigationController = UINavigationController(rootViewController: vc)
        navigationController.setNavigationBarHidden(true, animated: false)
        self.navigationController = navigationController
        rootViewController = vc
        return navigationController
    }
}

extension InstructorFlowCoordinator: InstructorListFlowDelegate {
    func onDetailTapped(with instructor: Instructor) {
        print("Instructor detail tapped: \(instructor.name) \(instructor.surname)")
        // Placeholder for future detail screen implementation
    }

    func onAddTapped() {
        print("Add new instructor tapped")
        // Placeholder for future add screen implementation
    }

    func onLoadError() {
        showErrorAlert(
            title: "Error",
            message: "Error occurred when loading instructors"
        )
    }
}
