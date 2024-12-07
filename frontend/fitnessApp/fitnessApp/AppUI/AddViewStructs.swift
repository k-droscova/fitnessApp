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
    var isDisabled: Bool = false
    
    var body: some View {
        Button(action: action) {
            Text("Save")
                .font(.headline)
                .padding(12)
                .padding(.horizontal, 12)
                .background(isDisabled ? Color.gray : Color.blue) // Add visual feedback
                .foregroundColor(.white)
                .cornerRadius(8)
        }
        .disabled(isDisabled)
    }
}

struct CancelButton: View {
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            Text("Cancel")
                .font(.headline)
                .padding(12)
                .padding(.horizontal, 8)
                .background(Color.gray.opacity(0.2))
                .foregroundColor(.red)
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

struct CancelAndSaveButtons: View {
    let cancelAction: () -> Void
    let saveAction: () -> Void
    var isSaveDisabled: Bool = false
    
    var body: some View {
        HStack {
            CancelButton(action: cancelAction)
            Spacer()
            SaveButton(action: saveAction, isDisabled: isSaveDisabled)
        }
        .padding(.horizontal, 16)
    }
}

struct FutureDatePicker: View {
    @Binding var selectedDate: Date
    let title: String
    
    var body: some View {
        VStack(alignment: .center, spacing: 8) {
            Text(title)
                .font(.headline)
            
            DatePicker(
                "",
                selection: $selectedDate,
                in: Date()...,
                displayedComponents: [.date, .hourAndMinute]
            )
            .labelsHidden()
            .datePickerStyle(GraphicalDatePickerStyle())
        }
        .padding()
        .background(RoundedRectangle(cornerRadius: 8).stroke(Color.gray.opacity(0.5)))
    }
}

struct ExpandableSelector<T: Hashable & CustomStringConvertible>: View {
    @Binding var selectedItem: T?
    let placeholder: String
    let options: [T]
    @State private var isExpanded: Bool = false
    
    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            // Main Selector Button
            Button(action: {
                withAnimation {
                    isExpanded.toggle()
                }
            }) {
                HStack {
                    if let selectedItem = selectedItem {
                        Text(selectedItem.description)
                            .foregroundColor(.primary)
                    } else {
                        Text(placeholder)
                            .foregroundColor(.secondary)
                    }
                    Spacer()
                    Image(systemName: isExpanded ? "chevron.up" : "chevron.down")
                        .foregroundColor(.gray)
                }
                .contentShape(Rectangle())
                .padding()
                .background(RoundedRectangle(cornerRadius: 8).stroke(Color.gray))
            }
            .buttonStyle(PlainButtonStyle())
            
            // Options List (conditionally visible)
            if isExpanded {
                VStack(alignment: .leading, spacing: 0) {
                    ForEach(options, id: \.self) { option in
                        Button(action: {
                            selectedItem = option
                            withAnimation {
                                isExpanded = false
                            }
                        }) {
                            Text(option.description)
                                .padding()
                                .frame(maxWidth: .infinity, alignment: .leading)
                                .contentShape(Rectangle())
                                .background(
                                    RoundedRectangle(cornerRadius: 8)
                                        .fill(option == selectedItem ? Color.blue.opacity(0.1) : Color.clear)
                                )
                        }
                        .buttonStyle(PlainButtonStyle())
                    }
                }
                .background(RoundedRectangle(cornerRadius: 8).stroke(Color.gray))
                .transition(.opacity)
            }
        }
        .padding(.horizontal)
    }
}

struct ExpandableDatePicker: View {
    @Binding var selectedDate: Date?
    var placeholder: String = "Select a date and time"
    @State private var isExpanded: Bool = false
    
    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            // Button to toggle the date picker
            Button(action: {
                withAnimation {
                    isExpanded.toggle()
                }
            }) {
                HStack {
                    if let date = selectedDate {
                        Text(date, formatter: dateFormatter)
                            .foregroundColor(.primary)
                    } else {
                        Text(placeholder)
                            .foregroundColor(.secondary)
                    }
                    Spacer()
                    Image(systemName: isExpanded ? "chevron.up" : "chevron.down")
                        .foregroundColor(.gray)
                }
                .contentShape(Rectangle())
                .padding()
                .background(RoundedRectangle(cornerRadius: 8).stroke(Color.gray))
            }
            .buttonStyle(PlainButtonStyle())
            
            // DatePicker (conditionally visible)
            if isExpanded {
                DatePicker(
                    "",
                    selection: Binding(
                        get: { selectedDate ?? Date().advanced(by: 3600) },
                        set: { newValue in
                            selectedDate = newValue
                        }
                    ),
                    in: Date()...,
                    displayedComponents: [.date, .hourAndMinute]
                )
                .datePickerStyle(GraphicalDatePickerStyle())
                .labelsHidden()
                .padding(.top, 8)
                .transition(.opacity)
            }
        }
        .padding(.horizontal)
    }
    
    // Date Formatter
    private var dateFormatter: DateFormatter {
        let formatter = DateFormatter()
        formatter.dateStyle = .short
        formatter.timeStyle = .short
        return formatter
    }
}

struct CapacityPicker: View {
    @Binding var selectedCapacity: Int
    let maxCapacity: Int
    let placeholder: String

    var body: some View {
        VStack(alignment: .center, spacing: 8) {
            Text("Capacity")
                .font(.headline)
                .foregroundColor(.primary)
            
            Picker(placeholder, selection: $selectedCapacity) {
                ForEach(1...maxCapacity, id: \.self) { capacity in
                    Text("\(capacity)").tag(capacity)
                }
            }
            .pickerStyle(WheelPickerStyle())
            .frame(height: 100)
            .clipped()
            .padding()
            .background(
                RoundedRectangle(cornerRadius: 8)
                    .stroke(Color.gray, lineWidth: 1)
            )
        }
        .padding()
    }
}
