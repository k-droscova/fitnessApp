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
            ListRowHeadlineStyleView(text: classType.name, alignment: .leading)
            
            ListRowSubheadlineStyleView(text: "\(classType.instructors.count)")
            
            ListRowSubheadlineStyleView(text: "\(classType.rooms.count)")
            
            ListRowSubheadlineStyleView(text: "\(classType.fitnessClasses.count)")
        }
        .listRowStyle()
    }
}
