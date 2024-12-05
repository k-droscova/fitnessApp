//
//  ClassTypeRow.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 05.12.2024.
//

import SwiftUI

struct ClassTypeRowView: View {
    private let classType: ClassType
    
    init (classType: ClassType) {
        self.classType = classType
    }
    
    var body: some View {
        HStack() {
            
        }
    }
}

struct ClassTypeRowView_Previews: PreviewProvider {
    static var previews: some View {
        ClassTypeRowView(classType: ClassType.mock)
            .previewLayout(.sizeThatFits)
    }
}
