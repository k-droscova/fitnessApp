//
//  TraineeEditView.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 07.12.2024.
//

import SwiftUI

struct TraineeEditView: View {
    @StateObject var viewModel: TraineeEditViewModel

    var body: some View {
        VStack(spacing: 16) {
            VStack(spacing: 16) {
                InputField(label: "First Name", text: $viewModel.name)
                InputField(label: "Last Name", text: $viewModel.surname)
                InputField(label: "Email", text: $viewModel.email)
            }
            .padding(.horizontal, 16)
            .padding(.top, 16)

            Spacer()
            
            SaveButton(
                action: viewModel.onSavePressed
            )
            .padding(.vertical, 8)
        }
        .onAppear {
            viewModel.onAppear()
        }
        .onDisappear {
            viewModel.onDisappear()
        }
        .overlay {
            if viewModel.isLoading {
                CustomProgressView()
            }
        }
    }
}
