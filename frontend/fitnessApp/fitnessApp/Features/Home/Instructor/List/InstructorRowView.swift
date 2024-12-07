//
//  InstructorRowView.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 07.12.2024.
//

import SwiftUI

struct InstructorRowView: View {
    private let instructor: Instructor
    @State private var isHighlighted: Bool = false
    
    init(instructor: Instructor) {
        self.instructor = instructor
    }
    
    var body: some View {
        HStack {
            // Instructor's name and surname
            ListRowHeadlineStyleView(text: "\(instructor.name) \(instructor.surname)", alignment: .leading)
            
            // Specialization count
            ListRowSubheadlineStyleView(text: "\(instructor.specializations.count)")
            
            // Classes count
            ListRowSubheadlineStyleView(text: "\(instructor.classes.count)")
        }
        .listRowStyle()
    }
}
