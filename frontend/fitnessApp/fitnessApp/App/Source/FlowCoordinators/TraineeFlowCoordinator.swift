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
    private var detailViewModel: TraineeDetailViewModel?
    private var addViewModel: TraineeAddViewModel?

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
        if self.detailViewModel != nil {
            self.detailViewModel = nil // prevents mem leaks
        }
        let vm = TraineeDetailViewModel(
            dependencies: appDependencies,
            trainee: trainee,
            delegate: self
        )
        self.detailViewModel = vm
        let vc = TraineeDetailView(viewModel: vm).hosting()
        presentSheet(vc, animated: true)
    }

    func onAddTapped() {
        let vm = TraineeAddViewModel(
            dependencies: appDependencies,
            delegate: self
        )
        self.addViewModel = vm
        let vc = TraineeAddView(viewModel: vm).hosting()
        presentNewScreen(vc, animated: true)
    }

    func onLoadError() {
        showErrorAlert(
            title: "Error",
            message: "Error occurred when loading trainees"
        )
    }
}

extension TraineeFlowCoordinator: TraineeDetaulViewFlowDelegate {
    func onEditPressed(trainee: Trainee) {
        print("Edit trainee tapped")
    }
    
    func onDeleteSuccess() {
        dismiss()
        listViewModel?.onAppear()
        showSuccessAlert(
            title: "Success",
            message: "Trainee deleted successfully"
        )
    }
    
    func onDeleteFailure() {
        showErrorAlert(
            title: "Delete error",
            message: "Error occured while deleting trainee, please try again"
        )
    }
}

extension TraineeFlowCoordinator: TraineeAddViewFlowDelegate {
    func onBackPressed() {
        popTopScreen(animated: true)
        self.addViewModel = nil
    }
    
    func onSaveSuccess() {
        popTopScreen(animated: true)
        self.addViewModel = nil
        listViewModel?.onAppear()
        showSuccessAlert(
            title: "Success",
            message: "Trainee saved successfully"
        )
    }
    
    func onSaveFailure(message: String) {
        showErrorAlert(
            title: "Save error",
            message: message
        )
    }
}
