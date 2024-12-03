//
//  OSLoggingService.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 03.12.2024.
//

import Foundation
import os.log

final class OSLoggerService: LoggerServicing {
    func _logMessage(_ message: String) {
        let log = OSLog(subsystem: Bundle.main.bundleIdentifier ?? "-", category: "General")
        os_log("%{public}@", log: log, type: .info, message)
    }
    
    func _logEvent(_ event: any Loggable) {
        let log = OSLog(subsystem: Bundle.main.bundleIdentifier ?? "-", category: event.context.rawValue)
        os_log("%{public}@", log: log, type: event.severity.osLogType, event.message)
    }
}
