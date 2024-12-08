//
//  InstructorDetailView.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 07.12.2024.
//

import SwiftUI

struct InstructorDetailView: View {
    @ObservedObject var viewModel: InstructorDetailViewModel

    var body: some View {
        VStack {
            Text(viewModel.instructor.description)
                .font(.headline)
                .padding(.top, 16)

            ScrollView {
                VStack(alignment: .leading, spacing: 16) {

                    ExpandableSection(
                        title: "Personal Details",
                        placeholder: "No personal details",
                        isEmpty: false
                    ) {
                        DetailRow(title: "Birthday", value:  viewModel.birthday)
                        DetailRow(title: "Age", value: "\(viewModel.instructor.age)")
                        DetailRow(title: "Total classes instructed", value: "\(viewModel.pastClasses.count)")
                    }
                    
                    ExpandableSection(
                        title: "Specializations",
                        placeholder: "No specializations yet",
                        isEmpty: viewModel.specializations.isEmpty
                    ) {
                        SpecializationSectionView(classTypes: viewModel.specializations)
                    }
                    
                    ExpandableSection(
                        title: "Upcoming classes",
                        placeholder: "No upcoming classes yet",
                        isEmpty: viewModel.futureClasses.isEmpty
                    ) {
                        FitnessClassSectionView(fitnessClasses: viewModel.futureClasses)
                    }
                    
                    ExpandableSection(
                        title: "Past classes",
                        placeholder: "No past classes yet",
                        isEmpty: viewModel.pastClasses.isEmpty
                    ) {
                        FitnessClassSectionView(fitnessClasses: viewModel.pastClasses)
                    }
                }
                .padding(.vertical, 16)
            }

            Spacer()

            EditAndDeleteButton(
                editAction: viewModel.onEditPressed,
                deleteAction: viewModel.onDeletePressed
            )
            .padding(.bottom, 16)

        }
        .onAppear {
            viewModel.onAppear()
        }
        .onDisappear {
            viewModel.onDisappear()
        }
        .navigationTitle("Instructor Details")
    }
}
