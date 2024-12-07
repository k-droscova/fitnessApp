//
//  TraieeRowView.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 07.12.2024.
//

import SwiftUI

struct TraineeRowView: View {
    private let trainee: Trainee
    @State private var isHighlighted: Bool = false
    
    init(trainee: Trainee) {
        self.trainee = trainee
    }
    
    var body: some View {
        HStack {
            // Trainee name
            ListRowHeadlineStyleView(text: "\(trainee.name) \(trainee.surname)", alignment: .leading)
            
            // Email
            ListRowSubheadlineStyleView(text: "\(trainee.email)", alignment: .trailing)
        }
        .listRowStyle()
    }
}
