//
//  Alert.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 03.12.2024.
//

import UIKit

class Alert {
    static func okAction(
        style: UIAlertAction.Style = .cancel,
        completion: (() -> Void)? = nil
    ) -> UIAlertAction {
        UIAlertAction(
            title: NSLocalizedString("alert.button.ok", comment: ""),
            style: style) { _ in
                completion?()
            }
    }
}
