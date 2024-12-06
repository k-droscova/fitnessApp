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
                    if !viewModel.rooms.isEmpty {
                        SectionHeaderView(title: "Rooms")
                        RoomSectionView(rooms: viewModel.rooms)
                    }

                    // Instructors Section
                    if !viewModel.instructors.isEmpty {
                        SectionHeaderView(title: "Instructors")
                        InstructorSectionView(instructors: viewModel.instructors)
                    }

                    // Fitness Classes Section
                    if !viewModel.fitnessClasses.isEmpty {
                        SectionHeaderView(title: "Fitness Classes")
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
        .navigationTitle("Class Type Details")
    }
}
