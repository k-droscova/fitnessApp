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
