//
//  ClassTypeView.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 05.12.2024.
//

import SwiftUI

struct ClassTypeDetailView: View {
    @ObservedObject var viewModel: ClassTypeDetailViewModel

    var body: some View {
        VStack {
            // Name in headline
            Text(viewModel.classTypeName)
                .font(.headline)
                .padding(.top, 16)

            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
                    // Rooms Section
                    if !viewModel.rooms.isEmpty {
                        SectionHeaderView(title: "Rooms")
                        RoomSectionView(rooms: viewModel.rooms)
                    }

                    // Instructors Section
                    if !viewModel.instructors.isEmpty {
                        SectionHeaderView(title: "Instructors")
                        InstructorSectionView(instructors: viewModel.instructors)
                    }

                    // Fitness Classes Section
                    if !viewModel.fitnessClasses.isEmpty {
                        SectionHeaderView(title: "Fitness Classes")
                        FitnessClassSectionView(fitnessClasses: viewModel.fitnessClasses)
                    }
                }
                .padding(.vertical, 16)
            }

            Spacer()

            // Dismiss Button
            DismissButton(action: {
                viewModel.dismissButtonPressed()
            })
        }
        .onAppear {
            viewModel.onAppear()
        }
        .navigationTitle("Class Type Details")
    }
}

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
                Text("\(instructor.name)")
                    .frame(maxWidth: .infinity, alignment: .leading)
                Text("\(instructor.surname)")
                    .frame(maxWidth: .infinity, alignment: .center)
                Text("\(instructor.email)")
                    .frame(maxWidth: .infinity, alignment: .trailing)
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

struct DismissButton: View {
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Text("Dismiss")
                .font(.headline)
                .frame(maxWidth: .infinity)
                .padding()
                .background(Color.blue)
                .foregroundColor(.white)
                .cornerRadius(8)
        }
        .padding(.horizontal, 16)
    }
}
