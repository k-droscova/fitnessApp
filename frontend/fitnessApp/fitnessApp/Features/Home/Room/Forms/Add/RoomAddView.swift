//
//  RoomAddView.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 07.12.2024.
//

import SwiftUI

struct RoomAddView: View {
    @StateObject var viewModel: RoomAddViewModel

    var body: some View {
        VStack(spacing: 16) {
            // Header with Back and Save buttons
            BackAndSaveButtons(
                backAction: viewModel.onBackPressed,
                saveAction: viewModel.onSavePressed
            )
            .padding(.top, 16)
            
            CapacityPicker(
                selectedCapacity: $viewModel.maxCapacity,
                maxCapacity: nil,
                placeholder: "Select Room Capacity"
            )
            .padding(.horizontal, 16)

            ScrollView {
                VStack(spacing: 16) {
                    // Selectable Class Types List
                    SelectableClassTypeList(classTypes: $viewModel.classTypes)
                        .padding(.horizontal, 16)
                }
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
