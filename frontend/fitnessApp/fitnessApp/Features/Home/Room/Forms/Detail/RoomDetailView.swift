//
//  RoomDetailView.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 07.12.2024.
//

import SwiftUI

struct RoomDetailView: View {
    @ObservedObject var viewModel: RoomDetailViewModel

    var body: some View {
        VStack {
            Text(viewModel.room.description)
                .font(.headline)
                .padding(.top, 16)

            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
                    
                    ExpandableSection(
                        title: "Room Details",
                        placeholder: "No details available",
                        isEmpty: false
                    ) {
                        DetailRow(title: "Capacity", value: "\(viewModel.room.maxCapacity)")
                        DetailRow(title: "Total Classes Hosted", value: "\(viewModel.pastClasses.count + viewModel.futureClasses.count)")
                    }
                    
                    ExpandableSection(
                        title: "Class Types",
                        placeholder: "No class types assigned",
                        isEmpty: viewModel.classTypes.isEmpty
                    ) {
                        SpecializationSectionView(classTypes: viewModel.classTypes)
                    }
                    
                    ExpandableSection(
                        title: "Upcoming Classes",
                        placeholder: "No upcoming classes",
                        isEmpty: viewModel.futureClasses.isEmpty
                    ) {
                        FitnessClassSectionView(fitnessClasses: viewModel.futureClasses)
                    }
                    
                    ExpandableSection(
                        title: "Past Classes",
                        placeholder: "No past classes",
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
        .navigationTitle("Room Details")
    }
}
