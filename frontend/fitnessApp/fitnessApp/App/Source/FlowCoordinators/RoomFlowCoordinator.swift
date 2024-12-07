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
        print("Room detail tapped: \(room.id), Capacity: \(room.maxCapacity)")
        // Placeholder for future detail screen implementation
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
