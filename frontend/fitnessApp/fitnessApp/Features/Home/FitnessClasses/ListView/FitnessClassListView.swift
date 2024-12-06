//
//  FitnessClassListView.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 06.12.2024.
//

import SwiftUI

struct FitnessClassListView: View {
    @StateObject var viewModel: FitnessClassListViewModel
    
    var body: some View {
        VStack {
            AddFormButton(action: viewModel.onAddButtonTapped)
                .padding(.bottom, 8)

            DateAndTimeFilters(
                fromDate: $viewModel.dateFromFilter,
                toDate: $viewModel.dateToFilter,
                fromTime: $viewModel.timeFromFilter,
                toTime: $viewModel.timeToFilter,
                errorText: viewModel.errorMessage
            )
            .padding(.horizontal, 8)
            
            ScrollView {
                LazyVStack(spacing: 10) {
                    if viewModel.filteredClasses.isEmpty {
                        Text("No fitness classes available")
                            .foregroundColor(.gray)
                            .padding()
                    } else {
                        ForEach(viewModel.filteredClasses) { fitnessClass in
                            Button(action: {
                                viewModel.onFitnessClassTapped(fitnessClass)
                            }) {
                                FitnessClassRow(
                                    fitnessClass: fitnessClass,
                                    classTypeName: viewModel.classTypeNameForFitnessClass(fitnessClass)
                                )
                                .padding(.horizontal, 8)
                            }
                            .buttonStyle(PlainButtonStyle())
                        }
                    }
                }
                .padding(.vertical, 10)
            }
        }
        .padding()
        .onAppear {
            viewModel.onAppear()
        }
    }
}
