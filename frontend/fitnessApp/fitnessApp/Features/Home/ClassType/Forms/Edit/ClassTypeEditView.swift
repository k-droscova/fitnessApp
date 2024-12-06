//
//  ClassTypeEditView.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 06.12.2024.
//

import SwiftUI

struct ClassTypeEditView: View {
    @StateObject var viewModel: ClassTypeEditViewModel

    var body: some View {
        VStack {
            // Header with Back and Save buttons
            CancelAndSaveButtons(
                cancelAction: viewModel.onCancelPressed,
                saveAction: viewModel.onSavePressed
            )
            .padding(.top, 16)

            // TextField for Class Type Name
            InputField(label: "Class Type Name", text: $viewModel.classTypeName)
                .padding(.horizontal, 16)
                .padding(.top, 16)

            ScrollView {
                VStack(spacing: 16) {
                    // Selectable Instructors List
                    SelectableInstructorList(instructors: $viewModel.instructors)

                    // Selectable Rooms List
                    SelectableRoomList(rooms: $viewModel.rooms)
                }
                .padding(.horizontal, 16)
                .padding(.top, 16)
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
