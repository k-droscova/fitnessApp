//
//  FitnessClassAddView.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 06.12.2024.
//

import SwiftUI

struct FitnessClassAddView: View {
    @StateObject var viewModel: FitnessClassAddViewModel

    var body: some View {
        VStack(spacing: 16) {
            BackAndSaveButtons(
                backAction: viewModel.onBackPressed,
                saveAction: viewModel.onSavePressed
            )
            .padding(.vertical, 16)
            .padding(.horizontal, 16)
            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
                    // Date and Time Picker
                    ExpandableDatePicker(
                        selectedDate: $viewModel.date,
                        placeholder: "Select Date and Time"
                    )
                    
                    // Class Type Selector
                    if viewModel.isClassTypeSelectionEnabled {
                        ExpandableSelector<ClassType>(
                            selectedItem: $viewModel.selectedClassType,
                            placeholder: "Select Class Type",
                            options: viewModel.classTypeOptions
                        )
                    }

                    // Room Selector
                    if viewModel.isRoomSelectionEnabled {
                        ExpandableSelector<Room>(
                            selectedItem: $viewModel.selectedRoom,
                            placeholder: "Select Room",
                            options: viewModel.roomOptions
                        )
                    }

                    // Instructor Selector
                    if viewModel.isInstructorSelectionEnabled {
                        ExpandableSelector<Instructor>(
                            selectedItem: $viewModel.selectedInstructor,
                            placeholder: "Select Instructor",
                            options: viewModel.instructorOptions
                        )
                    }

                    // Capacity Picker
                    if let maxCapacity = viewModel.selectedRoom?.maxCapacity {
                        CapacityPicker(
                            selectedCapacity: Binding(
                                get: { viewModel.selectedCapacity ?? 1 },
                                set: { viewModel.selectedCapacity = $0 }
                            ),
                            maxCapacity: maxCapacity,
                            placeholder: "Select Capacity"
                        )
                    }
                    
                    Spacer()
                }
                .padding(.horizontal)
            }

            Spacer()
        }
        .onAppear {
            viewModel.onAppear()
        }
        .navigationTitle("Add Fitness Class")
        .navigationBarTitleDisplayMode(.inline)
    }
}
