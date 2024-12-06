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
                .font(.title)
                .padding(.top, 16)
            
            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
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
                    
                    // Trainees Section
                    if !viewModel.trainees.isEmpty {
                        Divider()
                        SectionHeaderView(title: "Trainees")
                        VStack(alignment: .leading, spacing: 8) {
                            ForEach(viewModel.trainees) { trainee in
                                VStack(alignment: .leading, spacing: 4) {
                                    Text("\(trainee.name) \(trainee.surname)")
                                        .font(.body)
                                    Text(trainee.email)
                                        .font(.footnote)
                                        .foregroundColor(.gray)
                                }
                            }
                        }
                        .padding(.horizontal, 16)
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
        .padding()
    }
}
