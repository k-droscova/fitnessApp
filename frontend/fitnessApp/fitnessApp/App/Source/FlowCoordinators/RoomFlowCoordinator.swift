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
        print("Add new room tapped")
        // Placeholder for future add screen implementation
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
        print("Edit room tapped")
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
