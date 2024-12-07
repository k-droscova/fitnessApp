//
//  TraineeAddView.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 07.12.2024.
//

import SwiftUI

struct TraineeAddView: View {
    @StateObject var viewModel: TraineeAddViewModel

    var body: some View {
        VStack(spacing: 16) {
            // Header with Back and Save buttons
            BackAndSaveButtons(
                backAction: viewModel.onBackPressed,
                saveAction: viewModel.onSavePressed
            )
            .padding(.top, 16)

            // Input Fields
            ScrollView{
                VStack(spacing: 16) {
                    InputField(label: "First Name", text: $viewModel.name)
                    InputField(label: "Last Name", text: $viewModel.surname)
                    InputField(label: "Email", text: $viewModel.email)
                }
                .padding(.horizontal, 16)
            }

            Spacer()
        }
        .overlay {
            if viewModel.isLoading {
                CustomProgressView()
            }
        }
    }
}
