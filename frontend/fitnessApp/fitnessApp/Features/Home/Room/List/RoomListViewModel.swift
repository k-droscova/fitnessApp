//
//  RoomListViewModel.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 07.12.2024.
//

import Foundation
import Combine

protocol RoomListFlowDelegate: NSObject {
    func onDetailTapped(with room: Room)
    func onAddTapped()
    func onLoadError()
}

protocol RoomListViewModeling: BaseClass, ObservableObject {
    var rooms: [Room] { get }
    var searchRoomName: String { get set }
    func onAppear()
    func onDisappear()
    func fetchRooms()
    func onRoomTapped(_ room: Room)
    func onAddButtonTapped()
}

final class RoomListViewModel: BaseClass, RoomListViewModeling {
    typealias Dependencies = HasRoomManager
    
    private let manager: RoomManaging
    private weak var delegate: RoomListFlowDelegate?
    private var cancellables = Set<AnyCancellable>()
    
    @Published var rooms: [Room] = []
    @Published var searchRoomName: String = ""
    
    init(dependencies: Dependencies, delegate: RoomListFlowDelegate? = nil) {
        self.manager = dependencies.roomManager
        self.delegate = delegate
        
        super.init()
        setupSearch()
    }
    
    func onAppear() {
        if searchRoomName.isEmpty {
            fetchRooms()
        }
    }

    func onDisappear() {
        searchRoomName = ""
    }
    
    func fetchRooms() {
        Task { [weak self] in
            guard let self = self else { return }
            do {
                try await self.manager.fetchRooms()
            } catch {
                self.delegate?.onLoadError()
            }
            DispatchQueue.main.async {
                self.rooms = self.manager.allRooms
            }
        }
    }
    
    func onAddButtonTapped() {
        delegate?.onAddTapped()
    }
    
    func onRoomTapped(_ room: Room) {
        delegate?.onDetailTapped(with: room)
    }
    
    // MARK: - Private Helpers
    
    private func setupSearch() {
        $searchRoomName
            .debounce(for: .milliseconds(300), scheduler: DispatchQueue.main) // Adjust debounce delay as needed
            .removeDuplicates()
            .sink { [weak self] searchText in
                self?.performSearch(with: searchText)
            }
            .store(in: &cancellables)
    }
    
    private func performSearch(with input: String) {
        Task { [weak self] in
            guard let self = self else { return }
            do {
                if input.isEmpty {
                    // Reset to all rooms if search text is cleared
                    try await self.manager.fetchRooms()
                    DispatchQueue.main.async {
                        self.rooms = self.manager.allRooms
                    }
                } else {
                    // Perform the search using API
                    let searchResults = try await self.manager.fetchRoomsByClassTypeName(input)
                    DispatchQueue.main.async {
                        self.rooms = searchResults
                    }
                }
            } catch {
                DispatchQueue.main.async {
                    self.delegate?.onLoadError()
                }
            }
        }
    }
}
