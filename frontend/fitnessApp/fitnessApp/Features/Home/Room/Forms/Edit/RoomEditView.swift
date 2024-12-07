//
//  RoomEditView.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 07.12.2024.
//

import SwiftUI

struct RoomEditView: View {
    @StateObject var viewModel: RoomEditViewModel

    var body: some View {
        VStack(spacing: 16) {
            // Header with Cancel and Save buttons
            CancelAndSaveButtons(
                cancelAction: viewModel.onCancelPressed,
                saveAction: viewModel.onSavePressed
            )
            .padding(.top, 16)

            // Capacity Picker
            CapacityPicker(
                selectedCapacity: $viewModel.maxCapacity,
                maxCapacity: 100, // Adjust maxCapacity dynamically if needed
                placeholder: "Select Capacity"
            )
            .padding(.horizontal, 16)

            ScrollView {
                VStack(spacing: 16) {
                    // Selectable Class Types List
                    SelectableClassTypeList(classTypes: $viewModel.classTypes)
                }
                .padding(.horizontal, 16)
            }

            Spacer()
        }
        .onAppear {
            viewModel.onAppear()
        }
        .overlay {
            if viewModel.isLoading {
                CustomProgressView()
            }
        }
    }
}
