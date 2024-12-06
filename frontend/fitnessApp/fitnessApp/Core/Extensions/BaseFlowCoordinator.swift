//
//  BaseFlowCoordinator.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 03.12.2024.
//

import ACKategories
import Foundation
import UIKit


protocol BaseFlowCoordinator: Base.FlowCoordinatorNoDeepLink {
    func presentSheet(_ viewController: UIViewController, animated: Bool, completion: (() -> Void)?)
    func dismiss()
    func showAlert(title: String, message: String, completion: (() -> Void)?)
    func showAlert(title: String, message: String, actions: [UIAlertAction])
    func showSuccessAlert(title: String, message: String, completion: (() -> Void)?)
    func showErrorAlert(title: String, message: String, completion: (() -> Void)?)
    func onError(_ error: any Error)
    func stopChildCoordinators()
}

extension BaseFlowCoordinator {
    // MARK: - Presenting and Dismissing View Controllers
    
    func presentSheet(_ viewController: UIViewController, animated: Bool = true, completion: (() -> Void)? = nil) {
        DispatchQueue.main.async { [weak self] in
            guard let self = self, let rootVC = self.findSuitableController(from: self.rootViewController) else { return }
            rootVC.present(viewController, animated: animated, completion: completion)
        }
    }
    
    func dismiss() {
        DispatchQueue.main.async { [weak self] in
            self?.rootViewController.presentedViewController?.dismiss(animated: true)
        }
    }
    
    /// Finds the last view controller in the hierarchy that is not being dismissed. Should be called from `rootViewController`.
    private func findSuitableController(from controller: UIViewController) -> UIViewController? {
        if controller.isBeingDismissed {
            return nil
        }
        
        if let presented = controller.presentedViewController {
            return findSuitableController(from: presented) ?? controller
        }
        
        return controller
    }
    
    // MARK: - Presenting Alerts
    
    func showAlert(title: String, message: String, completion: (() -> Void)? = nil) {
        showAlert(
            title: title,
            message: message,
            actions: [UIAlertAction(title: "OK", style: .default, handler: { _ in completion?() })]
        )
    }
    
    func showAlert(title: String, message: String, actions: [UIAlertAction]) {
        DispatchQueue.main.async { [weak self] in
            let alertController = UIAlertController(
                title: title,
                message: message,
                preferredStyle: .alert
            )
            
            actions.forEach { alertController.addAction($0) }
            
            self?.presentSheet(alertController, animated: true)
        }
    }
    
    // MARK: - Success and Error Alerts
    
    func showSuccessAlert(title: String, message: String, completion: (() -> Void)? = nil) {
        showAlert(
            title: "✅ \(title)",
            message: message,
            completion: completion
        )
    }
    
    func showErrorAlert(title: String, message: String, completion: (() -> Void)? = nil) {
        showAlert(
            title: "❌ \(title)",
            message: message,
            completion: completion
        )
    }
    
    // MARK: - Error Handling
    
    func onError(_ error: any Error) {
        showErrorAlert(
            title: "Error",
            message: error.localizedDescription
        )
    }
    
    // MARK: - Stopping Child Coordinators
    
    func stopChildCoordinators() {
        childCoordinators.forEach { $0.stop(animated: false) }
    }
}
