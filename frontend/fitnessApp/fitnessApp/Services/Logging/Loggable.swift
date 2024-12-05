//
//  Loggable.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 03.12.2024.
//

import Foundation
import OSLog

protocol Loggable: CustomStringConvertible, Codable {
    var id: String { get }
    var message: String { get }
    var dateString: String { get }
    
    var source: LogSource { get }
    var severity: LogSeverity { get }
    var context: LogContext { get }
}

class LogEvent: Loggable {
    var id: String
    var message: String
    var dateString: String
    var source: LogSource
    var severity: LogSeverity
    var context: LogContext
    var description: String {
        (try? JsonMapper.jsonString(from: self)) ?? "Mapping Failed"
    }
    
    init(
        fileID: String = #fileID,
        fun: String = #function,
        line: Int = #line,
        message: String = "Unknown",
        context: LogContext = .system,
        severity: LogSeverity = .info,
        logger: LoggerServicing
    ) {
        self.id = UUID().uuidString
        self.source = .init(fileID: fileID, fun: fun, line: line)
        self.context = context
        self.message = message
        self.dateString = Date().ISO8601Format()
        self.severity = severity
        logger.logEvent(self)
    }
}

struct LogSource: Codable {
    var fileID: String
    var fun: String
    var line: Int
    
    init(fileID: String, fun: String, line: Int) {
        self.fileID = fileID
        self.fun = fun
        self.line = line
    }
}

enum LogSeverity: String, Codable {
    case debug, info, warning, error, fatal
    
    // Method to convert LogSeverity to OSLogType
    var osLogType: OSLogType {
        switch self {
        case .debug:
                .debug
        case .info:
                .info
        case .warning:
                .default // No direct mapping for warning, using default
        case .error:
                .error
        case .fatal:
                .fault
        }
    }
}

enum LogContext: String, Codable {
    case network
    case api
    case ui
    case database
    case system
}
