//
//  FitnessClassFlowCoordinator.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 06.12.2024.
//

import Foundation
import UIKit
import ACKategories

protocol FitnessClassFlowCoordinatorDelegate: NSObject {}

final class FitnessClassFlowCoordinator: Base.FlowCoordinatorNoDeepLink, BaseFlowCoordinator {
    private weak var delegate: FitnessClassFlowCoordinatorDelegate?
    private var listViewModel: FitnessClassListViewModel?
    private var detailViewModel: FitnessClassDetailViewModel?

    init(delegate: FitnessClassFlowCoordinatorDelegate? = nil) {
        self.delegate = delegate
        super.init()
    }
    
    override func start() -> UIViewController {
        super.start()
        
        let vm = FitnessClassListViewModel(
            dependencies: appDependencies,
            delegate: self
        )
        self.listViewModel = vm
        let vc = FitnessClassListView(viewModel: vm).hosting()
        let navigationController = UINavigationController(rootViewController: vc)
        navigationController.setNavigationBarHidden(true, animated: false)
        self.navigationController = navigationController
        rootViewController = vc
        return navigationController
    }
}

extension FitnessClassFlowCoordinator: FitnessClassListFlowDelegate {
    func onDetailTapped(with fitnessClass: FitnessClass) {
        if self.detailViewModel != nil {
            self.detailViewModel = nil // prevents mem leaks
        }
        let vm = FitnessClassDetailViewModel(
            dependencies: appDependencies,
            fitnessClass: fitnessClass,
            delegate: self
        )
        self.detailViewModel = vm
        let vc = FitnessClassDetailView(viewModel: vm).hosting()
        presentSheet(vc, animated: true)
    }
    
    func onAddTapped() {
        appDependencies.logger.logMessage("Add fitness class tapped")
        // Additional navigation logic can be added here in the future
    }
    
    func onLoadError() {
        showErrorAlert(
            title: "Error",
            message: "Error occurred while loading fitness classes."
        )
    }
}

extension FitnessClassFlowCoordinator: FitnessClassDetailFlowDelegate {
    func onEditPressed(fitnessClass: FitnessClass) {
        dismiss()
        appDependencies.logger.logMessage("Edit fitness class tapped")
    }
    
    func onDeleteSuccess() {
        dismiss()
        listViewModel?.onAppear()
        showSuccessAlert(
            title: "Success",
            message: "Fitness class deleted successfully"
        )
    }
    
    func onDeleteFailure() {
        showErrorAlert(
            title: "Delete error",
            message: "Error occured while deleting fitness class, please try again"
        )
    }
}
