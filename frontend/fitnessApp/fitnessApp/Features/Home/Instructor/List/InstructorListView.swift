//
//  InstructorListView.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 07.12.2024.
//

import SwiftUI

struct InstructorListView: View {
    @StateObject private var viewModel: InstructorListViewModel
    
    init(viewModel: InstructorListViewModel) {
        _viewModel = StateObject(wrappedValue: viewModel)
    }
    
    var body: some View {
        VStack {
            AddFormButton(action: viewModel.onAddButtonTapped)
            
            SearchBar(searchText: $viewModel.searchInstructorName)
                .padding(.top, 16)
            
            ListHeaderSection(headers: ["name", "specializations", "classes"])
            
            ListSection {
                ForEach(viewModel.instructors) { instructor in
                    Button(action: {
                        viewModel.onInstructorTapped(instructor)
                    }) {
                        InstructorRowView(instructor: instructor)
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
        .navigationTitle("Instructors")
    }
}
