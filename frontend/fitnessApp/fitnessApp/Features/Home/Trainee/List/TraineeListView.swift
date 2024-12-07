//
//  TraineeListView.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 07.12.2024.
//

import SwiftUI

struct TraineeListView: View {
    @StateObject private var viewModel: TraineeListViewModel
    
    init(viewModel: TraineeListViewModel) {
        _viewModel = StateObject(wrappedValue: viewModel)
    }
    
    var body: some View {
        VStack {
            // Add Button
            AddFormButton(action: viewModel.onAddButtonTapped)
            
            // Search Bar
            SearchBar(searchText: $viewModel.searchTraineeName)
                .padding(.top, 16)
            
            // Header Section
            ListHeaderSection(headers: ["name", "email"])
            
            // List Section
            ListSection {
                ForEach(viewModel.trainees) { trainee in
                    Button(action: {
                        viewModel.onTraineeTapped(trainee)
                    }) {
                        TraineeRowView(trainee: trainee)
                            .padding(.horizontal, 16)
                    }
                    .buttonStyle(PlainButtonStyle())
                }
            }
        }
        .onAppear {
            viewModel.onAppear()
        }
        .onDisappear {
            viewModel.onDisappear()
        }
        .navigationTitle("Trainees")
    }
}
