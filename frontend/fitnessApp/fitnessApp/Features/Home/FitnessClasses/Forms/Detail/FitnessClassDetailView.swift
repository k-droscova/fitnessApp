//
//  FitnessClassDetailView.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 06.12.2024.
//

import SwiftUI

struct FitnessClassDetailView: View {
    @ObservedObject var viewModel: FitnessClassDetailViewModel
    
    var body: some View {
        VStack {
            // Title
            Text(viewModel.dateTimeString)
                .font(.headline)
                .padding(.top, 16)
            
            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
                    
                    ExpandableSection(
                        title: "Class details",
                        placeholder: "No details available",
                        isEmpty: false
                    ) {
                        // Class Type Section
                        DetailRow(title: "Class Type", value: viewModel.classTypeName)
                        
                        // Instructor Section
                        if let instructor = viewModel.instructor {
                            DetailRow(title: "Instructor", value: "\(instructor.name) \(instructor.surname)")
                        }
                        
                        // Room Section
                        DetailRow(title: "Room number", value: String(viewModel.roomId))
                        
                        // Capacity Section
                        DetailRow(title: "Class Capacity", value: String(viewModel.capacity))
                        
                        // Occupancy Section
                        DetailRow(title: "Registrations", value: "\(viewModel.occupancy)/\(viewModel.capacity)")
                    }
                    
                    ExpandableSection(
                        title: "Registered trainees",
                        placeholder: "No trainees registered",
                        isEmpty: viewModel.trainees.isEmpty
                    ) {
                        TraineeSectionView(trainees: viewModel.trainees)
                    }
                }
                .padding(.vertical, 16)
            }
            
            Spacer()
            
            VStack {
                // Explanation text when editing is disabled
                if !viewModel.isEditingEnabled {
                    Text("You can only modify the class at least 1 day in advance.")
                        .font(.footnote)
                        .foregroundColor(.gray)
                        .padding(.bottom, 8)
                }
                
                // Edit and Delete Buttons
                EditAndDeleteButton(
                    editAction: viewModel.onEditPressed,
                    deleteAction: viewModel.onDeletePressed,
                    isEditDisabled: !viewModel.isEditingEnabled,
                    isDeleteDisabled: !viewModel.isEditingEnabled
                )
                .padding(.bottom, 16)
            }
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
