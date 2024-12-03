//
//  BaseClass.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 03.12.2024.
//

import Foundation

class BaseClass: NSObject {
    override init() {
        super.init()
        appDependencies.logger.logMessage("📱 👶 \(self)")
    }

    deinit {
        appDependencies.logger.logMessage("📱 ⚰️ \(self)")
    }
}
