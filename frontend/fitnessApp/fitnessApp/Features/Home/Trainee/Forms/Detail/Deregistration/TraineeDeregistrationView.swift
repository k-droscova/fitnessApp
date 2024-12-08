//
//  TraineeDeregistrationView.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 07.12.2024.
//

import SwiftUI

struct TraineeDeregistrationView: View {
    @StateObject var viewModel: TraineeDeregistrationViewModel
    
    var body: some View {
        VStack {
            // Date and Time Filters
            DateAndTimeFilters(
                fromDate: $viewModel.dateFromFilter,
                toDate: $viewModel.dateToFilter,
                fromTime: $viewModel.timeFromFilter,
                toTime: $viewModel.timeToFilter,
                dateFromRange: Calendar.current.startOfDay(for: Date()).addingTimeInterval(86400)...Date.distantFuture,
                dateToRange: Calendar.current.startOfDay(for: Date()).addingTimeInterval(86400)...Date.distantFuture,
                errorText: viewModel.errorMessage
            )
            .padding(.horizontal, 16)
            .padding(.top, 16)
            
            // Fitness Classes List
            ScrollView {
                LazyVStack(spacing: 10) {
                    if viewModel.filteredClasses.isEmpty {
                        Text("No fitness classes available")
                            .foregroundColor(.gray)
                            .padding()
                    } else {
                        ForEach(viewModel.filteredClasses) { fitnessClass in
                            FitnessClassDeregistrationRow(
                                fitnessClass: fitnessClass,
                                classTypeName: viewModel.classTypeName(for: fitnessClass),
                                isSelected: Binding(
                                    get: { viewModel.selectedClasses[fitnessClass.fitnessClassId ?? 0] ?? false },
                                    set: { viewModel.selectedClasses[fitnessClass.fitnessClassId ?? 0] = $0 }
                                )
                            )
                            .padding(.horizontal, 16)
                        }
                    }
                }
                .padding(.vertical, 10)
            }
            
            // Deregister Button
            Button(action: {
                viewModel.deregisterFromClasses()
            }) {
                Text("Deregister")
                    .font(.headline)
                    .padding()
                    .background(viewModel.isDeregisterButtonDisabled ? Color.gray : Color.red)
                    .foregroundColor(.white)
                    .cornerRadius(10)
                    .padding(.horizontal, 16)
            }
            .padding(.bottom, 16)
            .disabled(viewModel.isDeregisterButtonDisabled)
        }
        .onAppear {
            viewModel.onAppear()
        }
        .onDisappear {
            viewModel.onDisappear()
        }
        .overlay {
            if viewModel.isLoading {
                CustomProgressView()
            }
        }
    }
}
