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
            // Capacity Picker
            CapacityPicker(
                selectedCapacity: $viewModel.maxCapacity,
                maxCapacity: 100, // Adjust maxCapacity dynamically if needed
                placeholder: "Select Capacity"
            )
            .padding(.horizontal, 16)
            .padding(.top, 16)

            ScrollView {
                VStack(spacing: 16) {
                    // Selectable Class Types List
                    SelectableClassTypeList(classTypes: $viewModel.classTypes)
                }
                .padding(.horizontal, 16)
            }

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
