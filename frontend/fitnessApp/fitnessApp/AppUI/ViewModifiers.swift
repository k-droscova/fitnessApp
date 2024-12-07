//
//  ViewModifiers.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 07.12.2024.
//

import SwiftUI

struct ListRowViewModifier: ViewModifier {
    func body(content: Content) -> some View {
        content
            .padding(.horizontal, 8)
            .padding(.vertical, 8)
            .background(Color.gray.opacity(0.2))
            .cornerRadius(8)
    }
}

struct ListHeaderStyle: ViewModifier {
    func body(content: Content) -> some View {
        content
            .padding(.top, 16)
            .padding(.horizontal, 16)
    }
}
