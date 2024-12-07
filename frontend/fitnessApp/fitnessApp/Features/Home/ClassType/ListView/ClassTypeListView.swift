//
//  ClassTypeListView.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 05.12.2024.
//

import SwiftUI

struct ClassTypeListView: View {
    @StateObject private var viewModel: ClassTypeListViewModel
    
    init(viewModel: ClassTypeListViewModel) {
        _viewModel = StateObject(wrappedValue: viewModel)
    }
    
    var body: some View {
        VStack {
            AddFormButton(action: viewModel.onAddButtonTapped)
            
            SearchBar(searchText: $viewModel.searchClassTypeName)
                .padding(.top, 16)
            
            ListHeaderSection(headers: ["name", "instructors", "rooms", "classes"])
            
            ListSection {
                ForEach(viewModel.classTypes) { classType in
                    Button(action: {
                        viewModel.onClassTypeTapped(classType)
                    }) {
                        ClassTypeRowView(classType: classType)
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
        .navigationTitle("Class Types")
    }
}
