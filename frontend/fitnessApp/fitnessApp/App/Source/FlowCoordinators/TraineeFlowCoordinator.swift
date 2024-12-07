//
//  TraineeFlowCoordinator.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 07.12.2024.
//

import Foundation
import UIKit
import ACKategories

protocol TraineeFlowCoordinatorDelegate: NSObject {}

final class TraineeFlowCoordinator: Base.FlowCoordinatorNoDeepLink, BaseFlowCoordinator {
    private weak var delegate: TraineeFlowCoordinatorDelegate?
    private var listViewModel: TraineeListViewModel?

    init(delegate: TraineeFlowCoordinatorDelegate? = nil) {
        self.delegate = delegate
        super.init()
    }

    override func start() -> UIViewController {
        super.start()
        
        let vm = TraineeListViewModel(
            dependencies: appDependencies,
            delegate: self
        )
        self.listViewModel = vm
        let vc = TraineeListView(viewModel: vm).hosting()
        let navigationController = UINavigationController(rootViewController: vc)
        navigationController.setNavigationBarHidden(true, animated: false)
        self.navigationController = navigationController
        rootViewController = vc
        return navigationController
    }
}

extension TraineeFlowCoordinator: TraineeListFlowDelegate {
    func onDetailTapped(with trainee: Trainee) {
        print("Trainee detail tapped: \(trainee.name) \(trainee.surname)")
        // Placeholder for future detail screen implementation
    }

    func onAddTapped() {
        print("Add new trainee tapped")
        // Placeholder for future add screen implementation
    }

    func onLoadError() {
        showErrorAlert(
            title: "Error",
            message: "Error occurred when loading trainees"
        )
    }
}
