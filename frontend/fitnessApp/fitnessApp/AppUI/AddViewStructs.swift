//
//  AddViewStructs.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 06.12.2024.
//

import Foundation
import SwiftUI

struct SelectableRow: View {
    let title: String
    @Binding var isSelected: Bool
    
    var body: some View {
        HStack {
            Text(title)
                .frame(maxWidth: .infinity, alignment: .leading)
            Image(systemName: isSelected ? "checkmark.circle.fill" : "circle")
                .foregroundColor(isSelected ? .blue : .gray)
        }
        .contentShape(Rectangle())
        .onTapGesture {
            isSelected.toggle()
        }
        .padding(.vertical, 8)
    }
}

struct SelectableList<Item: Identifiable>: View {
    let title: String
    @Binding var items: [SelectableItem<Item>]
    
    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            SectionHeaderView(title: title)
            ForEach($items, id: \.item.id) { $selectableItem in
                SelectableRow(
                    title: selectableItem.itemDescription,
                    isSelected: $selectableItem.isSelected
                )
            }
        }
    }
}

struct SelectableItem<Item: Identifiable>: Identifiable {
    let id: Item.ID
    let item: Item
    var isSelected: Bool
    let itemDescription: String
    
    init(item: Item, isSelected: Bool = false, itemDescription: String) {
        self.id = item.id
        self.item = item
        self.isSelected = isSelected
        self.itemDescription = itemDescription
    }
}

struct SelectableInstructorList: View {
    @Binding var instructors: [SelectableItem<Instructor>]
    
    var body: some View {
        SelectableList(
            title: "Instructors",
            items: $instructors
        )
    }
}

struct SelectableRoomList: View {
    @Binding var rooms: [SelectableItem<Room>]
    
    var body: some View {
        SelectableList(
            title: "Rooms",
            items: $rooms
        )
    }
}

struct InputField: View {
    let label: String
    @Binding var text: String
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text(label)
                .font(.subheadline)
                .fontWeight(.semibold)
            TextField("Enter \(label.lowercased())", text: $text)
                .textFieldStyle(RoundedBorderTextFieldStyle())
        }
    }
}

struct BackButton: View {
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            HStack {
                Image(systemName: "arrow.left") // Left arrow icon
                    .font(.headline)
                Text("Back")
                    .font(.headline)
            }
            .padding(12)
            .background(Color.gray.opacity(0.2))
            .cornerRadius(8)
        }
    }
}

struct SaveButton: View {
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            Text("Save")
                .font(.headline)
                .padding(12)
                .padding(.horizontal, 12)
                .background(Color.blue)
                .foregroundColor(.white)
                .cornerRadius(8)
        }
    }
}

struct BackAndSaveButtons: View {
    let backAction: () -> Void
    let saveAction: () -> Void
    
    var body: some View {
        HStack {
            BackButton(action: backAction)
            Spacer()
            SaveButton(action: saveAction)
        }
        .padding(.horizontal, 16)
    }
}
