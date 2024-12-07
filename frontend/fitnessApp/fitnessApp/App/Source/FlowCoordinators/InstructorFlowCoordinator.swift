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
    private var detailViewModel: InstructorDetailViewModel?
    private var addViewModel: InstructorAddViewModel?

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
        if self.detailViewModel != nil {
            self.detailViewModel = nil // prevents mem leaks
        }
        let vm = InstructorDetailViewModel(
            dependencies: appDependencies,
            instructor: instructor,
            delegate: self
        )
        self.detailViewModel = vm
        let vc = InstructorDetailView(viewModel: vm).hosting()
        presentSheet(vc, animated: true)
    }

    func onAddTapped() {
        let vm = InstructorAddViewModel(
            dependencies: appDependencies,
            delegate: self
        )
        self.addViewModel = vm
        let vc = InstructorAddView(viewModel: vm).hosting()
        presentNewScreen(vc, animated: true)
    }

    func onLoadError() {
        showErrorAlert(
            title: "Error",
            message: "Error occurred when loading instructors"
        )
    }
}

extension InstructorFlowCoordinator: InstructorDetailViewFlowDelegate {
    func onEditPressed(instructor: Instructor) {
        print("Edit instructor tapped")
    }
    
    func onDeleteSuccess() {
        dismiss()
        listViewModel?.onAppear()
        showSuccessAlert(
            title: "Success",
            message: "Instructor deleted successfully"
        )
    }
    
    func onDeleteFailure() {
        showErrorAlert(
            title: "Delete error",
            message: "Error occured while deleting instructor, please try again"
        )
    }
}

extension InstructorFlowCoordinator: InstructorAddViewFlowDelegate {
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
            message: "Instructor saved successfully"
        )
    }
    
    func onSaveFailure(message: String) {
        showErrorAlert(
            title: "Save error",
            message: message
        )
    }
}
