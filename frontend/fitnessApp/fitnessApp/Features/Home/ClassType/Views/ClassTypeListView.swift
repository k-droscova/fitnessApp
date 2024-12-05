//
//  ClassTypeListView.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 05.12.2024.
//

import SwiftUI

struct ClassTypeListView: View {
    let classTypes: [ClassType]
    
    var body: some View {
        ScrollView {
            LazyVStack(spacing: 10) {
                ForEach(classTypes) { classType in
                    ClassTypeRowView(classType: classType)
                        .padding(.horizontal, 16)
                }
            }
            .padding(.vertical, 10)
        }
        .navigationTitle("Class Types")
    }
}

struct ClassTypeListView_Previews: PreviewProvider {
    static var previews: some View {
        ClassTypeListView(classTypes: [
            ClassType(id: 1, name: "Yoga", instructors: [1, 2], rooms: [101, 102], fitnessClasses: [201, 202]),
            ClassType(id: 2, name: "Pilates", instructors: [3], rooms: [103], fitnessClasses: [203, 204, 205]),
            ClassType(id: 3, name: "CrossFit", instructors: [4, 5, 6], rooms: [104, 105], fitnessClasses: [206]),
            ClassType(id: 4, name: "Zumba", instructors: [], rooms: [106], fitnessClasses: []),
            ClassType.mock2
        ])
    }
}
