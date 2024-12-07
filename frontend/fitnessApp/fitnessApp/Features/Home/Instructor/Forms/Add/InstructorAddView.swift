//
//  InstructorAddView.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 07.12.2024.
//

import SwiftUI

struct InstructorAddView: View {
    @StateObject var viewModel: InstructorAddViewModel

    var body: some View {
        VStack(spacing: 16) {
            // Header with Back and Save buttons
            BackAndSaveButtons(
                backAction: viewModel.onBackPressed,
                saveAction: viewModel.onSavePressed
            )
            .padding(.top, 16)
            
            // Name, Surname, Birthday
            VStack(spacing: 16) {
                InputField(label: "First Name", text: $viewModel.name)
                InputField(label: "Last Name", text: $viewModel.surname)
            }
            .padding(.horizontal, 16)
            
            ExpandableBirthdayPicker(
                selectedDate: $viewModel.birthdate,
                placeholder: "Select birthday"
            )
            
            ScrollView {
                VStack(spacing: 16) {
                    // Selectable Class Types List (Specializations)
                    SelectableClassTypeList(classTypes: $viewModel.specializations)
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
