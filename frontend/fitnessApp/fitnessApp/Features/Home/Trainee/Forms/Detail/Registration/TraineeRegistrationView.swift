//
//  TraineeRegistrationView.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 07.12.2024.
//

import SwiftUI

struct TraineeRegistrationView: View {
    @StateObject var viewModel: TraineeRegistrationViewModel
    
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
                            FitnessClassRegistrationRow(
                                fitnessClass: fitnessClass,
                                classTypeName: viewModel.classTypeName(for: fitnessClass),
                                isFull: viewModel.isClassFull(for: fitnessClass),
                                isRegistered: viewModel.isRegisteredForClass(for: fitnessClass),
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
            
            // Register Button
            Button(action: {
                viewModel.registerForClasses()
            }) {
                Text("Register")
                    .font(.headline)
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(viewModel.isRegisteredButtonDisabled ? Color.gray : Color.blue)
                    .foregroundColor(.white)
                    .cornerRadius(10)
                    .padding(.horizontal, 16)
            }
            .padding(.bottom, 16)
            .disabled(viewModel.isRegisteredButtonDisabled)
        }
        .onAppear {
            viewModel.onAppear()
        }
        .overlay {
            if viewModel.isLoading {
                CustomProgressView()
            }
        }
    }
}
