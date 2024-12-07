//
//  View.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 07.12.2024.
//

import SwiftUI

extension View {
    func listRowStyle() -> some View {
        self.modifier(ListRowViewModifier())
    }
}
