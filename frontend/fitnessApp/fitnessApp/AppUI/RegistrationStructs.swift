//
//  RegistrationStructs.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 07.12.2024.
//

import SwiftUI

struct FitnessClassRegistrationRow: View {
    let fitnessClass: FitnessClass
    let classTypeName: String
    let isFull: Bool
    let isRegistered: Bool
    @Binding var isSelected: Bool
    
    var body: some View {
        HStack {
            VStack(alignment: .leading, spacing: 4) {
                Text("\(fitnessClass.dateTime, style: .date), \(fitnessClass.dateTime, style: .time)")
                    .font(.headline)
                
                Text(classTypeName)
                    .font(.subheadline)
                    .foregroundColor(.secondary)
            }
            
            Spacer()
            
            if isRegistered {
                Text("Registered")
                    .font(.caption)
                    .foregroundColor(.green)
            } else if isFull {
                Text("Full")
                    .font(.caption)
                    .foregroundColor(.red)
            } else {
                Image(systemName: isSelected ? "checkmark.circle.fill" : "circle")
                    .foregroundColor(isSelected ? .blue : .gray)
            }
        }
        .contentShape(Rectangle())
        .padding(.vertical, 8)
        .padding(.horizontal, 16)
        .background(
            RoundedRectangle(cornerRadius: 10)
                .fill(isSelected ? Color.blue.opacity(0.1) : Color(.systemBackground))
                .shadow(color: Color.black.opacity(0.1), radius: 2, x: 0, y: 1)
        )
        .onTapGesture {
            if !isFull && !isRegistered {
                isSelected.toggle()
            }
        }
        .opacity(isFull || isRegistered ? 0.5 : 1.0) // Dim the row if full or registered
    }
}

struct FitnessClassDeregistrationRow: View {
    let fitnessClass: FitnessClass
    let classTypeName: String
    @Binding var isSelected: Bool
    
    var body: some View {
        HStack {
            VStack(alignment: .leading, spacing: 4) {
                Text("\(fitnessClass.dateTime, style: .date), \(fitnessClass.dateTime, style: .time)")
                    .font(.headline)
                
                Text(classTypeName)
                    .font(.subheadline)
                    .foregroundColor(.secondary)
            }
            
            Spacer()
            
            Image(systemName: isSelected ? "checkmark.circle.fill" : "circle")
                .foregroundColor(isSelected ? .red : .gray)
        }
        .contentShape(Rectangle())
        .padding(.vertical, 8)
        .padding(.horizontal, 16)
        .background(
            RoundedRectangle(cornerRadius: 10)
                .fill(isSelected ? Color.red.opacity(0.1) : Color(.systemBackground))
                .shadow(color: Color.black.opacity(0.1), radius: 2, x: 0, y: 1)
        )
        .onTapGesture {
            isSelected.toggle()
        }
    }
}

struct FitnessClassDeregistrationRow_Previews: PreviewProvider {
    struct PreviewWrapper: View {
        @State private var selectedStates: [Int: Bool] = [
            1: false,
            2: true,
            3: false,
            4: true
        ]
        
        var body: some View {
            VStack(spacing: 10) {
                FitnessClassDeregistrationRow(
                    fitnessClass: FitnessClass.mock, // Replace with your mock initializer
                    classTypeName: "Yoga",
                    isSelected: Binding(
                        get: { selectedStates[1] ?? false },
                        set: { selectedStates[1] = $0 }
                    )
                )
                
                FitnessClassDeregistrationRow(
                    fitnessClass: FitnessClass.mock,
                    classTypeName: "Pilates",
                    isSelected: Binding(
                        get: { selectedStates[2] ?? false },
                        set: { selectedStates[2] = $0 }
                    )
                )
                
                FitnessClassDeregistrationRow(
                    fitnessClass: FitnessClass.mock,
                    classTypeName: "Zumba",
                    isSelected: Binding(
                        get: { selectedStates[3] ?? false },
                        set: { selectedStates[3] = $0 }
                    )
                )
                
                FitnessClassDeregistrationRow(
                    fitnessClass: FitnessClass.mock,
                    classTypeName: "Spin",
                    isSelected: Binding(
                        get: { selectedStates[4] ?? false },
                        set: { selectedStates[4] = $0 }
                    )
                )
            }
            .padding()
            .background(Color(.systemGroupedBackground))
        }
    }
    
    static var previews: some View {
        PreviewWrapper()
            .previewLayout(.sizeThatFits)
    }
}
