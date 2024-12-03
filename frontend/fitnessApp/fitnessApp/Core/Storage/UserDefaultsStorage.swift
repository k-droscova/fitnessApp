//
//  UserDefaultsStorage.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 03.12.2024.
//

import Foundation

protocol HasUserDefaultsStoraging {
    var userDefaultsStorage: UserDefaultsStoraging { get set }
}

protocol UserDefaultsStoraging {
    var isLoggedIn: Bool { get set }
}

final class UserDefaultsStorage: UserDefaultsStoraging {
    typealias Dependencies = HasLoggerService
    
    // MARK: - Private Properties
    
    private let logger: LoggerServicing
    
    // MARK: - Public Properties
    
    var isLoggedIn: Bool {
        get {
            UserDefaults.standard.isLoggedIn
        }
        set {
            logger.logMessage("Setting isLoggedIn to \(newValue)")
            UserDefaults.standard.isLoggedIn = newValue
        }
    }
    
    // MARK: - Initialization
    
    init(dependencies: Dependencies) {
        self.logger = dependencies.logger
    }
}
