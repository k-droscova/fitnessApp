//
//  AppDelegate.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 03.12.2024.
//

import UIKit

@main
class AppDelegate: UIResponder, UIApplicationDelegate {
    
    var window: UIWindow?
    let appFlowCoordinator = AppFlowCoordinator()
    
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        // Set up UIPageControl appearance globally
        self.setupPageControlAppearance()
        
        // Initialize the main window and start the app flow
        let window = UIWindow(frame: UIScreen.main.bounds)
        appFlowCoordinator.start(in: window)
        self.window = window
        return true
    }
}

extension AppDelegate {
    private func setupPageControlAppearance() {
        UIPageControl.appearance().currentPageIndicatorTintColor = UIColor.purple // Active dot color
        UIPageControl.appearance().pageIndicatorTintColor = UIColor.lightGray // Inactive dot color
    }
}
