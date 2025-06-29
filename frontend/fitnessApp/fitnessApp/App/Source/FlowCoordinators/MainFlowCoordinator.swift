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
            image: UIImage(systemName: "figure.cooldown"),
            selectedImage: UIImage(systemName: "figure.cooldown.circle.fill")
        )
        
        // MARK: - Rooms
        let roomsFC = RoomFlowCoordinator(delegate: self)
        addChild(roomsFC)
        let roomsVC = roomsFC.start()
        roomsVC.tabBarItem = UITabBarItem(
            title: "Rooms",
            image: UIImage(systemName: "house"),
            selectedImage: UIImage(systemName: "house.circle.fill")
        )
        
        // MARK: - Instructors
        let instructorsFC = InstructorFlowCoordinator(delegate: self)
        addChild(instructorsFC)
        let instructorsVC = instructorsFC.start()
        instructorsVC.tabBarItem = UITabBarItem(
            title: "Instructors",
            image: UIImage(systemName: "person"),
            selectedImage: UIImage(systemName: "person.circle.fill")
        )
        
        // MARK: - Trainees
        let traineesFC = TraineeFlowCoordinator(delegate: self)
        addChild(traineesFC)
        let traineesVC = traineesFC.start()
        traineesVC.tabBarItem = UITabBarItem(
            title: "Trainees",
            image: UIImage(systemName: "figure.strengthtraining.traditional"),
            selectedImage: UIImage(systemName: "figure.strengthtraining.traditional.circle.fill")
        )
        
        // MARK: - Fitness Classes
        let fitnessClassesFC = FitnessClassFlowCoordinator(delegate: self)
        addChild(fitnessClassesFC)
        let fitnessClassesVC = fitnessClassesFC.start()
        fitnessClassesVC.tabBarItem = UITabBarItem(
            title: "Classes",
            image: UIImage(systemName: "list.bullet"),
            selectedImage: UIImage(systemName: "list.bullet.circle.fill")
        )
        
        // MARK: - SETUP TabBar
        let tabVC = UITabBarController()
        tabVC.viewControllers = [
            classTypeVC,
            roomsVC,
            instructorsVC,
            traineesVC,
            fitnessClassesVC
        ]
        tabVC.selectedViewController = instructorsVC
        customizeTabBarAppearance(tabBar: tabVC.tabBar)
        return tabVC
    }
    
    private func customizeTabBarAppearance(tabBar: UITabBar) {
        if #available(iOS 15.0, *) {
            let appearance = UITabBarAppearance()
            appearance.configureWithOpaqueBackground()
            appearance.backgroundColor = UIColor.systemBackground
            
            appearance.stackedLayoutAppearance.selected.iconColor = UIColor.systemBlue
            appearance.stackedLayoutAppearance.selected.titleTextAttributes = [NSAttributedString.Key.foregroundColor: UIColor.systemBlue]
            
            appearance.stackedLayoutAppearance.normal.iconColor = UIColor.systemGray
            appearance.stackedLayoutAppearance.normal.titleTextAttributes = [NSAttributedString.Key.foregroundColor: UIColor.systemGray]
            
            tabBar.standardAppearance = appearance
            tabBar.scrollEdgeAppearance = appearance
        } else {
            tabBar.barTintColor = UIColor.systemBackground
            tabBar.tintColor = UIColor.systemBlue
            tabBar.unselectedItemTintColor = UIColor.systemGray
        }
    }
}

extension MainFlowCoordinator: ClassTypeFlowCoordinatorDelegate {}

extension MainFlowCoordinator: FitnessClassFlowCoordinatorDelegate {}

extension MainFlowCoordinator: InstructorFlowCoordinatorDelegate {}

extension MainFlowCoordinator: RoomFlowCoordinatorDelegate {}

extension MainFlowCoordinator: TraineeFlowCoordinatorDelegate {}
