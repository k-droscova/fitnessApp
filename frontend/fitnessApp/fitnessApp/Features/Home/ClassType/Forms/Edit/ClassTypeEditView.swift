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
        VStack(spacing: 16) {
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
