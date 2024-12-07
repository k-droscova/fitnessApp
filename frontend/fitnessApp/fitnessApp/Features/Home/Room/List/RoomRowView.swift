//
//  RoomRowView.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 07.12.2024.
//

import SwiftUI

struct RoomRowView: View {
    private let room: Room
    @State private var isHighlighted: Bool = false
    
    init(room: Room) {
        self.room = room
    }
    
    var body: some View {
        HStack {
            // Room id
            ListRowHeadlineStyleView(text: "Room \(room.id)", alignment: .leading)
            
            // Capacity count
            ListRowSubheadlineStyleView(text: "\(room.maxCapacity)")
            
            // Class type count
            ListRowSubheadlineStyleView(text: "\(room.classTypes.count)")
            
            // Classes count
            ListRowSubheadlineStyleView(text: "\(room.classes.count)")
        }
        .listRowStyle()
    }
}
