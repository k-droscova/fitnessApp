//
//  Date.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 04.12.2024.
//

import Foundation

extension Date {
    
    // MARK: - Backend Interactions
    enum Backend {
        
        /// Combine `LocalDate` and `LocalTime` strings into a single `Date`
        static func combine(dateString: String, timeString: String) -> Date? {
            let combinedString = "\(dateString) \(timeString)"
            return Formatters.Backend.backendDateTimeFormatter.date(from: combinedString)
        }
        
        /// Split a `Date` into `LocalDate` and `LocalTime` strings
        static func split(dateTime: Date) -> (localDate: String, localTime: String) {
            let localDate = Formatters.Backend.localDateFormatter.string(from: dateTime)
            let localTime = Formatters.Backend.localTimeFormatter.string(from: dateTime)
            return (localDate, localTime)
        }
    }
    
    // MARK: - UI Interactions
    enum UI {
        
        /// Format a `Date` into a display-friendly date string `dd.MM.YYYY`
        static func formatDate(_ date: Date) -> String {
            Formatters.UI.displayDateFormatter.string(from: date)
        }
        
        /// Format a `Date` into a display-friendly time string `HH:mm`
        static func formatTime(_ date: Date) -> String {
            Formatters.UI.displayTimeFormatter.string(from: date)
        }
    }
}

enum Formatters {
    
    // MARK: - Backend Formatters
    enum Backend {
        /// Locale for Backend (ensure consistency with server expectations)
        private static let localeIdentifier = "en_US_POSIX"
        
        /// Formatter for `yyyy-MM-dd HH:mm:ss` (combines date and time for backend processing)
        static let backendDateTimeFormatter: DateFormatter = {
            let formatter = DateFormatter()
            formatter.dateFormat = "yyyy-MM-dd HH:mm:ss"
            formatter.locale = Locale(identifier: localeIdentifier)
            return formatter
        }()
        
        /// Formatter for `yyyy-MM-dd` (LocalDate format)
        static let localDateFormatter: DateFormatter = {
            let formatter = DateFormatter()
            formatter.dateFormat = "yyyy-MM-dd"
            formatter.locale = Locale(identifier: localeIdentifier)
            return formatter
        }()
        
        /// Formatter for `HH:mm:ss` (LocalTime format)
        static let localTimeFormatter: DateFormatter = {
            let formatter = DateFormatter()
            formatter.dateFormat = "HH:mm:ss"
            formatter.locale = Locale(identifier: localeIdentifier)
            return formatter
        }()
    }
    
    // MARK: - UI Formatters
    enum UI {
        /// Locale for UI (uses device's locale)
        private static let locale = Locale.current
        
        /// Formatter for `dd.MM.YYYY` (date format for display)
        static let displayDateFormatter: DateFormatter = {
            let formatter = DateFormatter()
            formatter.dateFormat = "dd.MM.yyyy"
            formatter.locale = locale
            return formatter
        }()
        
        /// Formatter for `HH:mm` (time format for display in 24-hour clock)
        static let displayTimeFormatter: DateFormatter = {
            let formatter = DateFormatter()
            formatter.dateFormat = "HH:mm"
            formatter.locale = locale
            return formatter
        }()
    }
}
