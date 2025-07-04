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
    private var addViewModel: FitnessClassAddViewModel?
    private var editViewModel: FitnessClassEditViewModel?
    
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
        let vm = FitnessClassAddViewModel(
            dependencies: appDependencies,
            delegate: self
        )
        self.addViewModel = vm
        let vc = FitnessClassAddView(viewModel: vm).hosting()
        presentNewScreen(vc, animated: true)
    }
    
    func onLoadError() {
        showErrorAlert(
            title: "Error",
            message: "Error occurred while loading fitness classes."
        )
    }
}

extension FitnessClassFlowCoordinator: FitnessClassDetailFlowDelegate {
    func onDetailDismissed() {
        self.detailViewModel = nil
        self.listViewModel?.onAppear()
    }
    
    func onEditPressed(fitnessClass: FitnessClass) {
        let vm = FitnessClassEditViewModel(
            dependencies: appDependencies,
            fitnessClass: fitnessClass,
            delegate: self
        )
        self.editViewModel = vm
        let vc = FitnessClassEditView(viewModel: vm).hosting()
        presentSheet(vc, animated: true)
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

extension FitnessClassFlowCoordinator: FitnessClassAddViewFlowDelegate {
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
            message: "Fitness class type saved successfully"
        )
    }
    
    func onSaveFailure(message: String) {
        showErrorAlert(
            title: "Save error",
            message: message
        )
    }
}

extension FitnessClassFlowCoordinator: FitnessClassEditViewFlowDelegate {
    func onEditViewDismissed() {
        self.editViewModel = nil
    }
    
    func onUpdateSuccess() {
        dismiss()
        self.editViewModel = nil
        detailViewModel?.onAppear()
        showSuccessAlert(
            title: "Success",
            message: "Fitness class updated successfully"
        )
    }
    
    func onUpdateFailure(message: String) {
        showErrorAlert(
            title: "Update error",
            message: message
        )
    }
}
