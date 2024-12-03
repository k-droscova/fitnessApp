//
//  ViewExtension.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 03.12.2024.
//

import Foundation
import SwiftUI

extension View {
    func hosting() -> UIHostingController<some View> {
        UIHostingController(rootView: self)
    }

    func hideKeyboard() {
        UIApplication.shared.sendAction(#selector(UIResponder.resignFirstResponder), to: nil, from: nil, for: nil)
    }
}
