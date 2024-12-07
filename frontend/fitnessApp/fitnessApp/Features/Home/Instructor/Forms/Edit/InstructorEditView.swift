//
//  InstructorEditView.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 07.12.2024.
//

import SwiftUI

struct InstructorEditView: View {
    @StateObject var viewModel: InstructorEditViewModel
    
    var body: some View {
        VStack(spacing: 16) {
            // Header with Cancel and Save buttons
            CancelAndSaveButtons(
                cancelAction: viewModel.onCancelPressed,
                saveAction: viewModel.onSavePressed
            )
            .padding(.top, 16)
            
            // Input Fields for Name and Surname
            VStack(spacing: 16) {
                InputField(label: "First Name", text: $viewModel.name)
                InputField(label: "Last Name", text: $viewModel.surname)
            }
            .padding(.horizontal, 16)
            
            // Expandable Birthday Picker
            ExpandableBirthdayPicker(
                selectedDate: $viewModel.birthdate,
                placeholder: "Select Birthday"
            )
            .padding(.horizontal, 16)
            
            // Scrollable List for Specializations
            ScrollView {
                VStack(spacing: 16) {
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
