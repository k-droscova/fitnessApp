//
//  AppDependencies.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 03.12.2024.
//

import Foundation

let appDependencies = AppDependencies()

final class AppDependencies {
    lazy var logger: LoggerServicing = OSLoggerService()
    lazy var userDefaultsStorage: UserDefaultsStoraging = UserDefaultsStorage(dependencies: self)
    lazy var userManager: UserManaging = UserManager(dependencies: self)
}

extension AppDependencies: HasLoggerService {}
extension AppDependencies: HasUserDefaultsStoraging {}
extension AppDependencies: HasUserManager {}
