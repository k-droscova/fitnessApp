//
//  TraineeDetailView.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 07.12.2024.
//

import SwiftUI

struct TraineeDetailView: View {
    @ObservedObject var viewModel: TraineeDetailViewModel

    var body: some View {
        VStack {
            Text("\(viewModel.trainee.name) \(viewModel.trainee.surname)")
                .font(.headline)
                .padding(.top, 16)

            Text(viewModel.trainee.email)
                .font(.subheadline)
                .foregroundColor(.gray)
                .padding(.bottom, 8)

            ScrollView {
                VStack(alignment: .center, spacing: 16) {

                    ExpandableSection(
                        title: "Personal Details",
                        placeholder: "No personal details available",
                        isEmpty: false
                    ) {
                        DetailRow(title: "Name", value: "\(viewModel.trainee.name) \(viewModel.trainee.surname)")
                        DetailRow(title: "Email", value: viewModel.trainee.email)
                        DetailRow(title: "Total classes attended", value: "\(viewModel.pastClasses.count)")
                        DetailRow(title: "Total upcoming classes", value: "\(viewModel.futureClasses.count)")
                    }
                    
                    ExpandableSection(
                        title: "Past Classes",
                        placeholder: "No past classes",
                        isEmpty: viewModel.pastClasses.isEmpty
                    ) {
                        FitnessClassSectionView(fitnessClasses: viewModel.pastClasses)
                    }
                    
                    ExpandableSection(
                        title: "Upcoming Classes",
                        placeholder: "No upcoming classes",
                        isEmpty: viewModel.futureClasses.isEmpty
                    ) {
                        FitnessClassSectionView(fitnessClasses: viewModel.futureClasses)
                    }
                    
                    VStack(alignment: .center) {
                        RegisterButton(action: viewModel.onRegisterPressed)
                        
                        UnregisterButton(
                            action: viewModel.onUnregisterPressed,
                            isDisabled: viewModel.isUnregisteredButtonDisabled
                        )
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
        .navigationTitle("Trainee Details")
    }
}
