//
//  LoggingService.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 03.12.2024.
//

import Foundation

protocol HasLoggerService {
    var logger: LoggerServicing { get }
}

protocol LoggerServicing {
    func _logMessage(_ message: String)
    func _logEvent(_ event: Loggable)
}

extension LoggerServicing {
    func logMessage(_ message: String, file: String = #file, function: String = #function, line: Int = #line) {
        let filename = (file as NSString).lastPathComponent
        let logMessage = "[\(filename):\(function):\(line)] \(message)"
        _logMessage(logMessage)
    }
    
    func logEvent(_ event: Loggable) {
        _logEvent(event)
    }
}
