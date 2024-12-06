//
//  ClassTypeFlowCoordinator.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 05.12.2024.
//

import Foundation
import UIKit
import ACKategories

protocol ClassTypeFlowCoordinatorDelegate: NSObject {}

final class ClassTypeFlowCoordinator: Base.FlowCoordinatorNoDeepLink, BaseFlowCoordinator {
    private weak var delegate: ClassTypeFlowCoordinatorDelegate?
    private var listViewModel: ClassTypeListViewModel?
    private var detailViewModel: ClassTypeDetailViewModel?
    //private var addViewModel: ClassTypeAddViewModel?
    
    init(delegate: ClassTypeFlowCoordinatorDelegate? = nil) {
        self.delegate = delegate
        super.init()
    }
    
    override func start() -> UIViewController {
        super.start()
        
        let vm = ClassTypeListViewModel(
            dependencies: appDependencies,
            delegate: self
        )
        self.listViewModel = vm
        let vc = ClassTypeListView(viewModel: vm).hosting()
        let navigationController = UINavigationController(rootViewController: vc)
        navigationController.setNavigationBarHidden(true, animated: false)
        self.navigationController = navigationController
        rootViewController = vc
        return navigationController
    }
}

extension ClassTypeFlowCoordinator: ClassTypeListFlowDelegate {
    func onDetailTapped(with classType: ClassType) {
        let vm = ClassTypeDetailViewModel(
            dependencies: appDependencies,
            classType: classType,
            delegate: self
        )
        self.detailViewModel = vm
        let vc = ClassTypeDetailView(viewModel: vm).hosting()
        presentSheet(vc, animated: true)
    }
    
    func onAddTapped() {
        appDependencies.logger.logMessage("Add tapped")
    }
}

extension ClassTypeFlowCoordinator: ClassTypeDetailViewFlowDelegate {
    func onLoadError() {
        showErrorAlert(
            title: "Error",
            message: "Error occurred when loading class type details"
        )
    }
    
    func onEditPressed(classType: ClassType) {
        appDependencies.logger.logMessage("Edit tapped for class type \(classType.name)")
    }
    
    func onDeleteSuccess() {
        dismiss()
        listViewModel?.onAppear()
        showSuccessAlert(
            title: "Success",
            message: "Class type deleted successfully"
        )
    }
    
    func onDeleteFailure() {
        showErrorAlert(
            title: "Delete error",
            message: "Error occured while deleting class type, please try again"
        )
    }
    
    func showDeleteConfirmation(_ confirmAction: @escaping () -> Void) {
        let confirmAlertAction = UIAlertAction(
            title: "Confirm",
            style: .destructive
        ) { _ in
            confirmAction()
        }
        
        let cancelAlertAction = UIAlertAction(
            title: "Cancel",
            style: .cancel,
            handler: nil
        )
        
        showAlert(
            title: "Are you sure?",
            message: "This action cannot be undone.",
            actions: [confirmAlertAction, cancelAlertAction]
        )
    }
}
