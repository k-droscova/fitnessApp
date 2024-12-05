//
//  ClassTypeRow.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 05.12.2024.
//

import SwiftUI

struct ClassTypeRowView: View {
    private let classType: ClassType
    @State private var isHighlighted: Bool = false
    
    init(classType: ClassType) {
        self.classType = classType
    }
    
    var body: some View {
        HStack {
            Text(classType.name)
                .font(.headline)
                .frame(maxWidth: .infinity, alignment: .leading) // Align name to the left
            
            Text("\(classType.instructors.count)")
                .font(.subheadline)
                .foregroundColor(.secondary)
                .frame(maxWidth: .infinity, alignment: .center) // Center instructors count
            
            Text("\(classType.rooms.count)")
                .font(.subheadline)
                .foregroundColor(.secondary)
                .frame(maxWidth: .infinity, alignment: .center) // Center rooms count
            
            Text("\(classType.fitnessClasses.count)")
                .font(.subheadline)
                .foregroundColor(.secondary)
                .frame(maxWidth: .infinity, alignment: .center) // Align fitness classes count to the right
        }
        .padding(.horizontal, 8)
        .padding(.vertical, 8) // Add some vertical padding for better readability
        .background(Color.gray.opacity(0.2))
        .cornerRadius(8)
    }
}
