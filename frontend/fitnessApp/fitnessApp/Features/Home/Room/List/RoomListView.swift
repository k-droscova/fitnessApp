//
//  RoomListView.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 07.12.2024.
//

import SwiftUI

struct RoomListView: View {
    @StateObject private var viewModel: RoomListViewModel
    
    init(viewModel: RoomListViewModel) {
        _viewModel = StateObject(wrappedValue: viewModel)
    }
    
    var body: some View {
        VStack {
            AddFormButton(action: viewModel.onAddButtonTapped)
            
            SearchBar(searchText: $viewModel.searchRoomName, placeholder: "Search rooms by class type name...")
                .padding(.top, 16)
            
            ListHeaderSection(headers: ["room #", "capacity", "class types", "classes"])
            
            ListSection {
                ForEach(viewModel.rooms) { room in
                    Button(action: {
                        viewModel.onRoomTapped(room)
                    }) {
                        RoomRowView(room: room)
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
        .navigationTitle("Rooms")
    }
}
