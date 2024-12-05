//
//  AppCoordinator.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 03.12.2024.
//

import Foundation
import UIKit
import ACKategories

final class AppFlowCoordinator: Base.FlowCoordinatorNoDeepLink, BaseFlowCoordinator {
    private weak var window: UIWindow?
    private weak var mainFC: MainFlowCoordinator?
    
    override func start(in window: UIWindow) {
        self.window = window
        super.start(in: window)
        appDependencies.userManager.delegate = self
        prepareWindow()
    }
    
    // MARK: - Private Helpers
    
    private func prepareWindow() {
        childCoordinators.forEach { $0.stop(animated: false) }
        showHome()
        /*if appDependencies.userManager.isLoggedIn {
            showHome()
        } else {
            showLogin()
        }*/
    }
    
    private func showHome() {
        let mainFC = MainFlowCoordinator()
        mainFC.delegate = self
        self.addChild(mainFC)
        self.mainFC = mainFC
        let mainVC = mainFC.start()
        
        window?.rootViewController = mainVC
        rootViewController = window?.rootViewController
        window?.makeKeyAndVisible()
    }
    
    private func showLogin() {
        
    }
    
    private func reload() {
        DispatchQueue.main.async { [weak self] in
            self?.prepareWindow()
        }
    }
}

extension AppFlowCoordinator: UserManagerFlowDelegate {
    func onLogout() {
        reload()
    }
}

extension AppFlowCoordinator: MainFlowCoordinatorDelegate {}
