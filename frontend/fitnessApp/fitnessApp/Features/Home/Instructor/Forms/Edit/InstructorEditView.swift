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
            // Input Fields for Name and Surname
            VStack(spacing: 16) {
                InputField(label: "First Name", text: $viewModel.name)
                InputField(label: "Last Name", text: $viewModel.surname)
            }
            .padding(.horizontal, 16)
            .padding(.top, 16)
            
            // Expandable Birthday Picker
            ExpandableBirthdayPicker(
                selectedDate: $viewModel.birthdate,
                placeholder: "Select Birthday"
            )
            .padding(.vertical, 8)
            // Scrollable List for Specializations
            ScrollView {
                VStack(spacing: 16) {
                    SelectableClassTypeList(classTypes: $viewModel.specializations)
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
