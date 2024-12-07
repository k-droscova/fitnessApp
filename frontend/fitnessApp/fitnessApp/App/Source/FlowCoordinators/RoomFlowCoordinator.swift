//
//  RoomFlowCoordinator.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 07.12.2024.
//

import Foundation
import UIKit
import ACKategories

protocol RoomFlowCoordinatorDelegate: NSObject {}

final class RoomFlowCoordinator: Base.FlowCoordinatorNoDeepLink, BaseFlowCoordinator {
    private weak var delegate: RoomFlowCoordinatorDelegate?
    private var listViewModel: RoomListViewModel?
    private var detailViewModel: RoomDetailViewModel?
    private var addViewModel: RoomAddViewModel?
    private var editViewModel: RoomEditViewModel?

    init(delegate: RoomFlowCoordinatorDelegate? = nil) {
        self.delegate = delegate
        super.init()
    }

    override func start() -> UIViewController {
        super.start()
        
        let vm = RoomListViewModel(
            dependencies: appDependencies,
            delegate: self
        )
        self.listViewModel = vm
        let vc = RoomListView(viewModel: vm).hosting()
        let navigationController = UINavigationController(rootViewController: vc)
        navigationController.setNavigationBarHidden(true, animated: false)
        self.navigationController = navigationController
        rootViewController = vc
        return navigationController
    }
}

extension RoomFlowCoordinator: RoomListFlowDelegate {
    func onDetailTapped(with room: Room) {
        if self.detailViewModel != nil {
            self.detailViewModel = nil // prevents mem leaks
        }
        let vm = RoomDetailViewModel(
            dependencies: appDependencies,
            room: room,
            delegate: self
        )
        self.detailViewModel = vm
        let vc = RoomDetailView(viewModel: vm).hosting()
        presentSheet(vc, animated: true)
    }

    func onAddTapped() {
        let vm = RoomAddViewModel(
            dependencies: appDependencies,
            delegate: self
        )
        self.addViewModel = vm
        let vc = RoomAddView(viewModel: vm).hosting()
        presentNewScreen(vc, animated: true)
    }

    func onLoadError() {
        showErrorAlert(
            title: "Error",
            message: "Error occurred when loading rooms"
        )
    }
}

extension RoomFlowCoordinator: RoomDetailViewFlowDelegate {
    func onEditPressed(room: Room) {
        dismiss()
        let vm = RoomEditViewModel(
            dependencies: appDependencies,
            room: room,
            delegate: self
        )
        self.editViewModel = vm
        let vc = RoomEditView(viewModel: vm).hosting()
        presentNewScreen(vc, animated: true)
    }
    
    func onDeleteSuccess() {
        dismiss()
        listViewModel?.onAppear()
        showSuccessAlert(
            title: "Success",
            message: "Room deleted successfully"
        )
    }
    
    func onDeleteFailure() {
        showErrorAlert(
            title: "Delete error",
            message: "Error occured while deleting room, please try again"
        )
    }
}

extension RoomFlowCoordinator: RoomAddViewFlowDelegate {
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
            message: "Room saved successfully"
        )
    }
    
    func onSaveFailure(message: String) {
        showErrorAlert(
            title: "Save error",
            message: message
        )
    }
}

extension RoomFlowCoordinator: RoomEditViewFlowDelegate {
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
            message: "Room updated successfully"
        )
    }
    
    func onUpdateFailure(message: String) {
        showErrorAlert(
            title: "Update error",
            message: message
        )
    }
}
