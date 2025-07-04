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
    var dateRange: ClosedRange<Date>? = nil
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
                .buttonStyle(PlainButtonStyle())

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
                        get: { selectedDate ?? (dateRange?.lowerBound ?? Date()) },
                        set: {
                            selectedDate = $0
                            withAnimation {
                                isDatePickerVisible = false
                            }
                        }
                    ),
                    in: dateRange ?? Date.distantPast...Date.distantFuture,
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
    var timeRange: ClosedRange<Date>? = nil
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
                .buttonStyle(PlainButtonStyle())

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
                        get: { selectedTime ?? (timeRange?.lowerBound ?? Date()) },
                        set: {
                            selectedTime = $0
                            withAnimation {
                                isTimePickerVisible = false
                            }
                        }
                    ),
                    in: timeRange ?? Date.distantPast...Date.distantFuture,
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
    var dateFromRange: ClosedRange<Date>? = nil
    var dateToRange: ClosedRange<Date>? = nil
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
                    FilterableDatePicker(selectedDate: $fromDate, placeholder: "Date From", dateRange: dateFromRange)
                    FilterableDatePicker(selectedDate: $toDate, placeholder: "Date To", dateRange: dateToRange)
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

struct ListRowHeadlineStyleView: View {
    let text: String
    var alignment: Alignment = .leading
    
    var body: some View {
        Text(text)
            .font(.headline)
            .frame(maxWidth: .infinity, alignment: alignment)
    }
}


struct ListRowSubheadlineStyleView: View {
    let text: String
    var alignment: Alignment = .center
    
    var body: some View {
        Text(text)
            .font(.subheadline)
            .foregroundColor(.secondary)
            .frame(maxWidth: .infinity, alignment: alignment)
    }
}

struct ListHeaderSection: View {
    let headers: [String]
    
    var body: some View {
        HStack {
            ForEach(headers, id: \.self) { header in
                Text(header)
                    .font(.subheadline)
                    .foregroundColor(.secondary)
                    .frame(maxWidth: .infinity, alignment: .center)
            }
        }
        .modifier(ListHeaderStyle())
    }
}

struct ListSection<Content: View>: View {
    let content: Content
    
    init(@ViewBuilder content: () -> Content) {
        self.content = content()
    }
    
    var body: some View {
        ScrollView {
            LazyVStack(spacing: 10) {
                content
            }
            .padding(.vertical, 10)
        }
    }
}

struct SearchBar: View {
    @Binding var searchText: String
    var placeholder: String = "Search by name..."
    
    var body: some View {
        HStack {
            TextField(placeholder, text: $searchText)
                .padding(8)
                .padding(.horizontal, 24)
                .background(Color.gray.opacity(0.2))
                .cornerRadius(8)
                .overlay(
                    HStack {
                        Image(systemName: "magnifyingglass")
                            .foregroundColor(.gray)
                            .frame(minWidth: 0, maxWidth: .infinity, alignment: .leading)
                            .padding(.leading, 8)
                        
                        if !searchText.isEmpty {
                            Button(action: {
                                searchText = ""
                            }) {
                                Image(systemName: "xmark.circle.fill")
                                    .foregroundColor(.gray)
                                    .padding(.trailing, 8)
                            }
                        }
                    }
                )
        }
        .padding(.horizontal, 16)
    }
}

struct RegisterButton: View {
    let action: () -> Void
    var isDisabled: Bool = false

    var body: some View {
        Button(action: action) {
            Text("Register for classes")
                .font(.subheadline)
                .padding()
                .background(isDisabled ? Color(.secondarySystemBackground) : Color.clear)
                .foregroundColor(isDisabled ? Color.secondary : Color.blue)
                .cornerRadius(8)
        }
        .disabled(isDisabled)
    }
}

struct UnregisterButton: View {
    let action: () -> Void
    var isDisabled: Bool = false

    var body: some View {
        Button(action: action) {
            Text("Unregister from classes")
                .font(.subheadline)
                .padding()
                .background(isDisabled ? Color(.secondarySystemBackground) : Color.clear)
                .foregroundColor(isDisabled ? Color.secondary : Color.red)
                .cornerRadius(8)
        }
        .disabled(isDisabled)
    }
}

import SwiftUI

struct ButtonsPreviewProvider: PreviewProvider {
    static var previews: some View {
        VStack(spacing: 16) {
            RegisterButton(action: {
                print("Register button tapped")
            }, isDisabled: false)

            RegisterButton(action: {
                print("Register button tapped (disabled)")
            }, isDisabled: true)

            UnregisterButton(action: {
                print("Unregister button tapped")
            }, isDisabled: false)

            UnregisterButton(action: {
                print("Unregister button tapped (disabled)")
            }, isDisabled: true)
        }
        .previewLayout(.sizeThatFits)
    }
}
