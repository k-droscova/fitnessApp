//
//  Calendar.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 06.12.2024.
//

import Foundation

extension Calendar {
    func isDateInFuture(_ date: Date) -> Bool {
        guard let tomorrow = self.date(byAdding: .day, value: 1, to: Date()) else {
            return false
        }
        return date >= tomorrow
    }
}
