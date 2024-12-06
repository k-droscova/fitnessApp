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
    var isDisabled: Bool = false

    var body: some View {
        Button(action: action) {
            Text("Delete")
                .font(.headline)
                .frame(maxWidth: .infinity)
                .padding()
                .background(isDisabled ? Color.gray : Color.red)
                .foregroundColor(.white)
                .cornerRadius(8)
        }
        .disabled(isDisabled)
    }
}

struct EditButton: View {
    let action: () -> Void
    var isDisabled: Bool = false

    var body: some View {
        Button(action: action) {
            Text("Edit")
                .font(.headline)
                .frame(maxWidth: .infinity)
                .padding()
                .background(isDisabled ? Color.gray : Color.blue)
                .foregroundColor(.white)
                .cornerRadius(8)
        }
        .disabled(isDisabled)
    }
}

struct EditAndDeleteButton: View {
    let editAction: () -> Void
    let deleteAction: () -> Void
    var isEditDisabled: Bool = false
    var isDeleteDisabled: Bool = false

    var body: some View {
        HStack(spacing: 16) {
            EditButton(action: editAction, isDisabled: isEditDisabled)
            DeleteButton(action: deleteAction, isDisabled: isDeleteDisabled)
        }
        .padding(.horizontal, 16)
    }
}

struct DetailRow: View {
    let title: String
    let value: String

    var body: some View {
        HStack {
            Text(title)
                .font(.headline)
            Spacer()
            Text(value)
                .font(.body)
                .foregroundColor(.secondary)
        }
        .padding(.horizontal, 16)
    }
}
