//
//  MainFlowCoordinator.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 03.12.2024.
//

import Foundation
import UIKit
import ACKategories
import SwiftUI

protocol MainFlowCoordinatorDelegate: NSObject {}

final class MainFlowCoordinator: Base.FlowCoordinatorNoDeepLink, BaseFlowCoordinator {
    weak var delegate: MainFlowCoordinatorDelegate?
    
    override init() {
        super.init()
    }
    
    override func start() -> UIViewController {
        super.start()
        let tabBarVC = setupTabBar()
        let navigationController = UINavigationController(rootViewController: tabBarVC)
        navigationController.setNavigationBarHidden(true, animated: false)
        self.navigationController = navigationController
        rootViewController = tabBarVC
        
        return navigationController
    }
    
    // MARK: - Private helpers
    
    private func setupTabBar() -> UITabBarController{
        // MARK: - Class Types
        let classTypeFC = ClassTypeFlowCoordinator(delegate: self)
        addChild(classTypeFC)
        let classTypeVC = classTypeFC.start()
        classTypeVC.tabBarItem = UITabBarItem(
            title: "Class Types",
            image: UIImage(systemName: "figure.yoga"),
            selectedImage: UIImage(systemName: "figure.yoga.circle.fill")
        )
        
        // MARK: - Fitness Classes
        let fitnessClassesFC = FitnessClassFlowCoordinator(delegate: self)
        addChild(fitnessClassesFC)
        let fitnessClassesVC = fitnessClassesFC.start()
        fitnessClassesVC.tabBarItem = UITabBarItem(
            title: "Fitness Classes",
            image: UIImage(systemName: "list.bullet"),
            selectedImage: UIImage(systemName: "list.bullet.circle.fill")
        )
        
        // MARK: - SETUP TabBar
        let tabVC = UITabBarController()
        tabVC.viewControllers = [
            classTypeVC,
            fitnessClassesVC
        ]
        tabVC.selectedViewController = fitnessClassesVC
        //customizeTabBarAppearance(tabBar: tabVC.tabBar)
        return tabVC
    }
    
    /*private func customizeTabBarAppearance(tabBar: UITabBar) {
     if #available(iOS 15.0, *) {
     let appearance = UITabBarAppearance()
     appearance.configureWithOpaqueBackground()
     appearance.backgroundColor = CustomColors.TabBar.background.color
     
     appearance.stackedLayoutAppearance.selected.iconColor = CustomColors.TabBar.selectedItem.color
     appearance.stackedLayoutAppearance.selected.titleTextAttributes = [NSAttributedString.Key.foregroundColor: CustomColors.TabBar.selectedItem.color]
     
     appearance.stackedLayoutAppearance.normal.iconColor = CustomColors.TabBar.unselectedItem.color
     appearance.stackedLayoutAppearance.normal.titleTextAttributes = [NSAttributedString.Key.foregroundColor: CustomColors.TabBar.unselectedItem.color]
     
     tabBar.standardAppearance = appearance
     tabBar.scrollEdgeAppearance = appearance
     } else {
     tabBar.barTintColor = CustomColors.TabBar.background.color
     tabBar.tintColor = CustomColors.TabBar.selectedItem.color
     tabBar.unselectedItemTintColor = CustomColors.TabBar.unselectedItem.color
     }
     }
     */
}

extension MainFlowCoordinator: ClassTypeFlowCoordinatorDelegate {}

extension MainFlowCoordinator: FitnessClassFlowCoordinatorDelegate {}
