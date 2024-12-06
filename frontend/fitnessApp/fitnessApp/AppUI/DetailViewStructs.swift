//
//  ListStructs.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 06.12.2024.
//

import Foundation
import SwiftUI

struct SectionHeaderView: View {
    let title: String

    var body: some View {
        Text(title)
            .font(.subheadline)
            .fontWeight(.bold)
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(.horizontal, 16)
    }
}

struct RoomSectionView: View {
    let rooms: [Room]

    var body: some View {
        ForEach(rooms) { room in
            HStack {
                Text("Room \(room.roomId ?? 0)")
                    .frame(maxWidth: .infinity, alignment: .leading)
                Text("Capacity: \(room.maxCapacity)")
                    .frame(maxWidth: .infinity, alignment: .trailing)
            }
            .padding(.horizontal, 16)
        }
    }
}

struct InstructorSectionView: View {
    let instructors: [Instructor]

    var body: some View {
        ForEach(instructors) { instructor in
            HStack {
                Text("\(instructor.name) \(instructor.surname)")
                    .frame(maxWidth: .infinity, alignment: .leading)
            }
            .padding(.horizontal, 16)
        }
    }
}

struct FitnessClassSectionView: View {
    let fitnessClasses: [FitnessClass]

    var body: some View {
        ForEach(fitnessClasses) { fitnessClass in
            HStack {
                Text(Date.UI.formatDate(fitnessClass.dateTime))
                    .frame(maxWidth: .infinity, alignment: .leading)
                Text(Date.UI.formatTime(fitnessClass.dateTime))
                    .frame(maxWidth: .infinity, alignment: .center)
                Text("Trainees: \(fitnessClass.trainees.count)")
                    .frame(maxWidth: .infinity, alignment: .trailing)
            }
            .padding(.horizontal, 16)
        }
    }
}

struct DeleteButton: View {
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Text("Delete")
                .font(.headline)
                .frame(maxWidth: .infinity)
                .padding()
                .background(Color.red)
                .foregroundColor(.white)
                .cornerRadius(8)
        }
    }
}

struct EditButton: View {
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            Text("Edit")
                .font(.headline)
                .frame(maxWidth: .infinity)
                .padding()
                .background(Color.blue)
                .foregroundColor(.white)
                .cornerRadius(8)
        }
    }
}

struct EditAndDeleteButton: View {
    let editAction: () -> Void
    let deleteAction: () -> Void
    
    var body: some View {
        HStack(spacing: 16) {
            EditButton(action: editAction)
            DeleteButton(action: deleteAction)
        }
        .padding(.horizontal, 16)
    }
}


struct EditAndDeleteButton_Previews: PreviewProvider {
    static var previews: some View {
        EditAndDeleteButton(
            editAction: {
                print("Edit action triggered")
            },
            deleteAction: {
                print("Delete action triggered")
            }
        )
        .previewLayout(.sizeThatFits)
    }
}
