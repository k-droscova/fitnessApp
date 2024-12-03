//
//  UserDefaults.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 03.12.2024.
//

import Foundation

extension UserDefaults {
    /// Enum representing the keys used to store values in UserDefaults.
    private enum Keys: String {
        case isLoggedIn
    }

    var isLoggedIn: Bool {
        get {
            object(forKey: Keys.isLoggedIn.rawValue) as? Bool ?? false
        }
        set {
            set(newValue, forKey: Keys.isLoggedIn.rawValue)
        }
    }
}
