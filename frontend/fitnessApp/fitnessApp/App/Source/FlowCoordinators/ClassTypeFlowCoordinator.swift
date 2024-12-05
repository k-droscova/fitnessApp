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
        present(vc, animated: true)
    }
    
    func onAddTapped() {
        appDependencies.logger.logMessage("Add tapped")
    }
}

extension ClassTypeFlowCoordinator: ClassTypeDetailViewFlowDelegate {
    func onLoadError() {
        showAlert(
            titleKey: "alert.load_error.title",
            messageKey: "alert.load_error.message"
        )
    }
    
    func dismissPressed() {
        dismiss()
    }
    
    func onEditSuccess() {
        dismiss()
        showAlert(
            titleKey: "alert.edit_success.title",
            messageKey: "alert.edit_success.message"
        )
    }
    
    func onEditFailure() {
        showAlert(
            titleKey: "alert.edit_failure.title",
            messageKey: "alert.edit_failure.message"
        )
    }
    
    func onDeleteSuccess() {
        dismiss()
        showAlert(
            titleKey: "alert.delete_success.title",
            messageKey: "alert.delete_success.message"
        )
    }
    
    func onDeleteFailure() {
        showAlert(
            titleKey: "alert.delete_failure.title",
            messageKey: "alert.delete_failure.message"
        )
    }
    
    func showDeleteConfirmation(_ confirmAction: @escaping () -> Void) {
        let confirmAlertAction = UIAlertAction(
            title: NSLocalizedString("alert.delete_confirm.ok", comment: ""),
            style: .destructive
        ) { _ in
            confirmAction()
        }
        
        let cancelAlertAction = UIAlertAction(
            title: NSLocalizedString("alert.delete_confirm.cancel", comment: ""),
            style: .cancel,
            handler: nil
        )
        
        showAlert(
            titleKey: "alert.delete_confirm.title",
            messageKey: "alert.delete_confirm.message",
            actions: [confirmAlertAction, cancelAlertAction]
        )
    }
}
