//
//  SharedStructs.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 06.12.2024.
//

import SwiftUI

struct CustomProgressView: View {
    var body: some View {
        ProgressView("Loading...")
            .progressViewStyle(CircularProgressViewStyle())
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .background(Color.black.opacity(0.3))
            .ignoresSafeArea()
    }
}

struct FilterableDatePicker: View {
    @Binding var selectedDate: Date?
    let placeholder: String
    @State private var isDatePickerVisible: Bool = false
    
    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            HStack {
                Button(action: {
                    withAnimation {
                        isDatePickerVisible.toggle()
                    }
                }) {
                    HStack {
                        if let date = selectedDate {
                            Text(date, style: .date)
                                .foregroundColor(.primary)
                        } else {
                            Text(placeholder)
                                .foregroundColor(.secondary)
                        }
                        Spacer()
                    }
                    .contentShape(Rectangle())
                }
                .buttonStyle(PlainButtonStyle()) // Ensure no default button styles
                
                if selectedDate != nil {
                    Button(action: {
                        selectedDate = nil
                        isDatePickerVisible = false
                    }) {
                        Image(systemName: "xmark.circle.fill")
                            .foregroundColor(.red)
                    }
                    .buttonStyle(BorderlessButtonStyle())
                }
            }
            .padding()
            .background(RoundedRectangle(cornerRadius: 8).stroke(Color.gray))
            
            if isDatePickerVisible {
                DatePicker(
                    "",
                    selection: Binding(
                        get: { selectedDate ?? Date() },
                        set: {
                            selectedDate = $0
                            withAnimation {
                                isDatePickerVisible = false
                            }
                        }
                    ),
                    displayedComponents: [.date]
                )
                .datePickerStyle(GraphicalDatePickerStyle())
                .labelsHidden()
                .transition(.opacity)
            }
        }
    }
}

struct FilterableTimePicker: View {
    @Binding var selectedTime: Date?
    let placeholder: String
    @State private var isTimePickerVisible: Bool = false
    
    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            HStack {
                Button(action: {
                    withAnimation {
                        isTimePickerVisible.toggle()
                    }
                }) {
                    HStack {
                        if let time = selectedTime {
                            Text(time, style: .time)
                                .foregroundColor(.primary)
                        } else {
                            Text(placeholder)
                                .foregroundColor(.secondary)
                        }
                        Spacer()
                    }
                    .contentShape(Rectangle())
                }
                .buttonStyle(PlainButtonStyle()) // Ensure no default button styles
                
                if selectedTime != nil {
                    Button(action: {
                        selectedTime = nil
                        isTimePickerVisible = false
                    }) {
                        Image(systemName: "xmark.circle.fill")
                            .foregroundColor(.red)
                    }
                    .buttonStyle(BorderlessButtonStyle())
                }
            }
            .padding()
            .background(RoundedRectangle(cornerRadius: 8).stroke(Color.gray))
            
            if isTimePickerVisible {
                DatePicker(
                    "",
                    selection: Binding(
                        get: { selectedTime ?? Date() },
                        set: {
                            selectedTime = $0
                            withAnimation {
                                isTimePickerVisible = false
                            }
                        }
                    ),
                    displayedComponents: [.hourAndMinute]
                )
                .datePickerStyle(WheelDatePickerStyle())
                .labelsHidden()
                .transition(.opacity)
            }
        }
    }
}

struct DateAndTimeFilters: View {
    @Binding var fromDate: Date?
    @Binding var toDate: Date?
    @Binding var fromTime: Date?
    @Binding var toTime: Date?
    @State private var areFiltersVisible: Bool = false
    let errorText: String?
    
    var body: some View {
        VStack(spacing: 16) {
            // Expand/Collapse Button
            Button(action: {
                withAnimation {
                    areFiltersVisible.toggle()
                }
            }) {
                HStack {
                    Text(areFiltersVisible ? "Hide Filters" : "Expand Filters")
                        .font(.headline)
                    Spacer()
                    Image(systemName: areFiltersVisible ? "chevron.up" : "chevron.down")
                        .foregroundColor(.gray)
                }
                .padding()
                .background(RoundedRectangle(cornerRadius: 8).fill(Color.gray.opacity(0.1)))
            }
            
            // Date and Time Pickers (conditionally visible)
            if areFiltersVisible {
                VStack(spacing: 16) {
                    FilterableDatePicker(selectedDate: $fromDate, placeholder: "Date From")
                    FilterableDatePicker(selectedDate: $toDate, placeholder: "Date To")
                    FilterableTimePicker(selectedTime: $fromTime, placeholder: "Time From")
                    FilterableTimePicker(selectedTime: $toTime, placeholder: "Time To")
                }
                .transition(.opacity)
            }
            
            // Error Text
            if let error = errorText, !error.isEmpty {
                Text(error)
                    .foregroundColor(.red)
                    .font(.subheadline)
                    .padding(.horizontal)
            }
        }
    }
}

struct AddFormButton: View {
    let action: () -> Void
    
    var body: some View {
        HStack {
            Spacer()
            Button(action: action) {
                Image(systemName: "plus.circle.fill")
                    .font(.title)
                    .foregroundColor(.blue)
            }
            .padding(.trailing, 16)
        }
    }
}
