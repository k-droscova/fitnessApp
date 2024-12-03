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
        // MARK: - SETUP TabBar
        let tabVC = UITabBarController()
        tabVC.viewControllers = []
        //tabVC.selectedViewController = homeVC
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

/*extension MainFlowCoordinator {
    enum Constants {
        enum TabBar {
            enum Home {
                case selected, unselected
                var icon: UIImage? {
                    switch self {
                    case .selected:
                        UIImage(named: CustomImages.TabBar.Home.pink.fullPath)
                    case .unselected:
                        UIImage(named: CustomImages.TabBar.Home.pink.fullPath)
                    }
                }
            }
            enum Rank {
                case selected, unselected
                var icon: UIImage? {
                    switch self {
                    case .selected:
                        UIImage(named: CustomImages.TabBar.Rank.pink.fullPath)
                    case .unselected:
                        UIImage(named: CustomImages.TabBar.Rank.black.fullPath)
                    }
                }
            }
            enum Points {
                case selected, unselected
                var icon: UIImage? {
                    switch self {
                    case .selected:
                        UIImage(named: CustomImages.TabBar.Profile.pink.fullPath)
                    case .unselected:
                        UIImage(named: CustomImages.TabBar.Profile.black.fullPath)
                    }
                }
            }
            enum Map {
                case selected, unselected
                var icon: UIImage? {
                    switch self {
                    case .selected:
                        UIImage(named: CustomImages.TabBar.Map.pink.fullPath)
                    case .unselected:
                        UIImage(named: CustomImages.TabBar.Map.black.fullPath)
                    }
                }
            }
        }
    }
}
*/
