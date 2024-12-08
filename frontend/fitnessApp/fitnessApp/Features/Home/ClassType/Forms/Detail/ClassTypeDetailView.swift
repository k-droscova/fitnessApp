//
//  ClassTypeView.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 05.12.2024.
//

import SwiftUI

struct ClassTypeDetailView: View {
    @ObservedObject var viewModel: ClassTypeDetailViewModel

    var body: some View {
        VStack {
            // Name in headline
            Text(viewModel.classTypeName)
                .font(.headline)
                .padding(.top, 16)

            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
                    // Rooms Section
                    ExpandableSection(
                        title: "Rooms",
                        placeholder: "No dedicated rooms for this class type",
                        isEmpty: viewModel.rooms.isEmpty
                    ) {
                        RoomSectionView(rooms: viewModel.rooms)
                    }
                    
                    // Instructors
                    ExpandableSection(
                        title: "Instructors",
                        placeholder: "No specialized instructors for this class type",
                        isEmpty: viewModel.instructors.isEmpty
                    ) {
                        InstructorSectionView(instructors: viewModel.instructors)
                    }
                    
                    // Classes
                    ExpandableSection(
                        title: "Fitness Classes",
                        placeholder: "No scheduled classes for this class type",
                        isEmpty: viewModel.fitnessClasses.isEmpty
                    ) {
                        FitnessClassSectionView(fitnessClasses: viewModel.fitnessClasses)
                    }
                }
                .padding(.vertical, 16)
            }

            Spacer()

            EditAndDeleteButton(
                editAction: viewModel.editButtonPressed,
                deleteAction: viewModel.deleteButtonPressed
            )
            .padding(.bottom, 16)

        }
        .onAppear {
            viewModel.onAppear()
        }
        .onDisappear {
            viewModel.onDisappear()
        }
        .navigationTitle("Class Type Details")
    }
}
