//
//  FitnessClassRowView.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 06.12.2024.
//

import SwiftUI

struct FitnessClassRow: View {
    let fitnessClass: FitnessClass
    let classTypeName: String

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            // First Line: Date, Time and Instructor ID
            HStack {
                Text("\(fitnessClass.dateTime, style: .date), \(fitnessClass.dateTime, style: .time)")
                    .font(.headline)
                Spacer()
                Text("Instructor ID: \(fitnessClass.instructor)")
                    .font(.subheadline)
                    .foregroundColor(.secondary)
            }
            
            // Second Line: Class Type and Capacity
            HStack {
                Text("Class Type: \(classTypeName)")
                    .font(.subheadline)
                    .foregroundColor(.primary)
                Spacer()
                Text("Capacity: \(fitnessClass.trainees.count)/\(fitnessClass.capacity)")
                    .font(.subheadline)
                    .foregroundColor(.secondary)
            }
        }
        .padding(.horizontal, 8)
        .padding(.vertical, 8) // Add some vertical padding for better readability
        .background(Color.gray.opacity(0.2))
        .cornerRadius(8)
    }
}

struct FitnessClassRowPreview: View {
    var body: some View {
        let fitnessClass = FitnessClass.mock
        
        return VStack(spacing: 16) {
            FitnessClassRow(fitnessClass: fitnessClass, classTypeName: "Yoga")
            FitnessClassRow(fitnessClass: FitnessClass.mock2, classTypeName: "Pilates")
        }
        .padding()
    }
}

struct FitnessClassRowPreview_Previews: PreviewProvider {
    static var previews: some View {
        FitnessClassRowPreview()
            .previewLayout(.sizeThatFits)
    }
}
