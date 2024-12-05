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
            HStack {
                Spacer()
                Button(action: {
                    viewModel.onAddButtonTapped()
                }) {
                    Image(systemName: "plus.circle.fill")
                        .font(.title)
                        .foregroundColor(.blue)
                }
                .padding(.trailing, 16)
            }
            
            HStack {
                Text("name")
                    .font(.subheadline)
                    .foregroundColor(.secondary)
                    .frame(maxWidth: .infinity, alignment: .center) // Align name to the left
                
                Text("instructors")
                    .font(.subheadline)
                    .foregroundColor(.secondary)
                    .frame(maxWidth: .infinity, alignment: .center) // Center instructors count
                
                Text("rooms")
                    .font(.subheadline)
                    .foregroundColor(.secondary)
                    .frame(maxWidth: .infinity, alignment: .center) // Center rooms count
                
                Text("classes")
                    .font(.subheadline)
                    .foregroundColor(.secondary)
                    .frame(maxWidth: .infinity, alignment: .center) // Align fitness classes count to the right
            }
            .padding(.top, 16)
            .padding(.horizontal, 16)
            ScrollView {
                LazyVStack(spacing: 10) {
                    ForEach(viewModel.classTypes) { classType in
                        Button(action: {
                            viewModel.onClassTypeTapped(classType)
                        }) {
                            ClassTypeRowView(classType: classType)
                                .padding(.horizontal, 16)
                        }
                        .buttonStyle(PlainButtonStyle()) // To remove default button styling
                    }
                }
                .padding(.vertical, 10)
            }
        }
        .onAppear {
            viewModel.onAppear()
        }
        .navigationTitle("Class Types")
    }
}
