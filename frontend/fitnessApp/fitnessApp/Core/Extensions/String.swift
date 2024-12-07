//
//  String.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 04.12.2024.
//

import Foundation

extension String {
    /// Appends query items to the current string, treating it as a URL path.
    /// - Parameter queryItems: Dictionary of query parameters and their values.
    /// - Returns: A string with the query items appended or the original string if invalid.
    func appendQueryItems(_ queryItems: [String: String?]) -> String {
        var urlComponents = URLComponents(string: self) ?? URLComponents()
        urlComponents.queryItems = queryItems.compactMap { key, value in
            guard let value = value else { return nil }
            return URLQueryItem(name: key, value: value)
        }
        return urlComponents.url?.absoluteString ?? self
    }
    
    /// Appends a single query item to the current string, treating it as a URL path.
    /// - Parameters:
    ///   - name: The name of the query parameter.
    ///   - value: The value of the query parameter.
    /// - Returns: A string with the query item appended or the original string if invalid.
    func appendQueryItem(name: String, value: String?) -> String {
        guard let value = value else { return self }
        return appendQueryItems([name: value])
    }
    
    var isValidEmail: Bool {
        let detector = try? NSDataDetector(types: NSTextCheckingResult.CheckingType.link.rawValue)
        let range = NSRange(location: 0, length: self.utf16.count)
        let matches = detector?.matches(in: self, options: [], range: range) ?? []

        // Ensure there's exactly one match and that it's an email
        return matches.count == 1 && matches.first?.url?.scheme == "mailto"
    }
}
