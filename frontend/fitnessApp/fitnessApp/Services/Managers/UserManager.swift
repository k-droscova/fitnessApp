//
//  UserManager.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 03.12.2024.
//

import Foundation

protocol UserManagerFlowDelegate: NSObject {
    func onLogout()
}

protocol HasUserManager {
    var userManager: UserManaging { get }
}

protocol UserManaging {
    var delegate: UserManagerFlowDelegate? { get set }
    
    var isLoggedIn: Bool { get }
    func login(credentials: LoginCredentials) async throws
    func logout()
}

final class UserManager: BaseClass, UserManaging {
    typealias Dependencies = HasLoggerService & HasUserDefaultsStoraging
    
    // MARK: - Private Properties
    
    private let logger: LoggerServicing
    private var userDefaultsStorage: UserDefaultsStoraging
    
    // MARK: - Public Properties
    
    weak var delegate: UserManagerFlowDelegate?
    var isLoggedIn: Bool { userDefaultsStorage.isLoggedIn }
    
    // MARK: - Initialization
    
    init(dependencies: Dependencies) {
        self.logger = dependencies.logger
        self.userDefaultsStorage = dependencies.userDefaultsStorage
    }
    
    // MARK: - Public Interface
    
    func login(credentials: LoginCredentials) async throws {
        userDefaultsStorage.isLoggedIn = true
    }
    
    func logout() {
        userDefaultsStorage.isLoggedIn = false
        delegate?.onLogout()
    }
}
