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
    private var addViewModel: ClassTypeAddViewModel?
    private var editViewModel: ClassTypeEditViewModel?
    
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
        if self.detailViewModel != nil {
            self.detailViewModel = nil // prevents mem leaks
        }
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
        let vm = ClassTypeAddViewModel(
            dependencies: appDependencies,
            delegate: self
        )
        self.addViewModel = vm
        let vc = ClassTypeAddView(viewModel: vm).hosting()
        presentNewScreen(vc, animated: true)
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
        dismiss()
        let vm = ClassTypeEditViewModel(
            dependencies: appDependencies,
            classType: classType,
            delegate: self
        )
        self.editViewModel = vm
        let vc = ClassTypeEditView(viewModel: vm).hosting()
        presentNewScreen(vc, animated: true)
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
}

extension ClassTypeFlowCoordinator: ClassTypeAddViewFlowDelegate {
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
            message: "Class type saved successfully"
        )
    }
    
    func onSaveFailure(message: String) {
        showErrorAlert(
            title: "Save error",
            message: message
        )
    }
}

extension ClassTypeFlowCoordinator: ClassTypeEditViewFlowDelegate {
    func onCancelPressed() {
        popTopScreen(animated: true)
        self.editViewModel = nil
    }
    
    func onUpdateSuccess() {
        popTopScreen(animated: true)
        self.addViewModel = nil
        listViewModel?.onAppear()
        showSuccessAlert(
            title: "Success",
            message: "Class type updated successfully"
        )
    }
    
    func onUpdateFailure(message: String) {
        showErrorAlert(
            title: "Update error",
            message: message
        )
    }
}
