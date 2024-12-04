//
//  Optional.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 04.12.2024.
//

import Foundation

extension Optional where Wrapped == String {
    
    /// Returns `true` if the optional string is non-nil and not empty
    var isNotEmpty: Bool {
        guard let self = self else { return false }
        return !self.isEmpty
    }

    /// Returns an empty string if the optional is nil, otherwise returns the string
    var emptyIfNil: String {
        self ?? ""
    }
}
