//
//  NewClassTypeFormView.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 05.12.2024.
//

import SwiftUI

struct ClassTypeAddView: View {
    @StateObject var viewModel: ClassTypeAddViewModel
    
    var body: some View {
        VStack {
            // Header with Back and Save buttons
            BackAndSaveButtons(
                backAction: viewModel.onBackPressed,
                saveAction: viewModel.onSavePressed
            )
            .padding(.top, 16)
            
            // TextField for Class Type Name
            InputField(label: "Class Type Name", text: $viewModel.classTypeName)
                .padding(.horizontal, 16)
                .padding(.top, 16)
            
            // Selectable Instructors List
            ScrollView {
                VStack(spacing: 16) {
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
                ProgressView("Loading...")
                    .progressViewStyle(CircularProgressViewStyle())
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                    .background(Color.black.opacity(0.5))
                    .ignoresSafeArea()
            }
        }
    }
}
