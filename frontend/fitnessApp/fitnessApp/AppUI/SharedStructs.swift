//
//  SharedStructs.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 06.12.2024.
//

import SwiftUI

struct CustomProgressView: View {
    var body: some View {
        ProgressView("Loading...")
            .progressViewStyle(CircularProgressViewStyle())
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .background(Color.black.opacity(0.3))
            .ignoresSafeArea()
    }
}
