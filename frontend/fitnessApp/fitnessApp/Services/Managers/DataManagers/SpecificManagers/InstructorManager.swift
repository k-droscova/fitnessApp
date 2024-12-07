//
//  InstructorManager.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 04.12.2024.
//

import Foundation

protocol InstructorManagerFlowDelegate: NSObject {}

protocol HasInstructorManager {
    var instructorManager: InstructorManaging { get }
}

protocol InstructorManaging {
    var delegate: InstructorManagerFlowDelegate? { get set }
    var allInstructors: [Instructor] { get }
    func fetchInstructors() async throws
    func fetchInstructorById(_ id: Int) async throws -> Instructor
    func createInstructor(_ instructor: Instructor) async throws
    func updateInstructor(_ id: Int, _ newInstructor: Instructor) async throws
    func deleteInstructor(_ id: Int) async throws
    func fetchAvailableInstructors(classTypeId: Int?, date: String, time: String) async throws -> [Int]
    func fetchInstructorsByName(name: String?, surname: String?, input: String?) async throws -> [Instructor]
}

final class InstructorManager: BaseClass, InstructorManaging {
    typealias Dependencies = HasLoggerService & HasInstructorAPIService
    
    weak var delegate: InstructorManagerFlowDelegate?
    private(set) var allInstructors: [Instructor] = []
    
    private let logger: LoggerServicing
    private let instructorAPIService: InstructorAPIServicing
    
    init(dependencies: Dependencies) {
        self.logger = dependencies.logger
        self.instructorAPIService = dependencies.instructorAPIService
    }
    
    func fetchInstructors() async throws {
        allInstructors = try await instructorAPIService.allInstructors()
    }
    
    func fetchInstructorById(_ id: Int) async throws -> Instructor {
        return try await instructorAPIService.instructor(id)
    }
    
    func createInstructor(_ instructor: Instructor) async throws {
        try await instructorAPIService.postNewInstructor(instructor)
        allInstructors.append(instructor)
    }
    
    func updateInstructor(_ id: Int, _ newInstructor: Instructor) async throws {
        try await instructorAPIService.updateInstructor(id, newInstructor)
        guard let index = allInstructors.firstIndex(where: { $0.instructorId == id }) else {
            throw BaseError(
                context: .system,
                message: "Instructor with id \(id) not found in local cache",
                logger: logger
            )
        }
        allInstructors[index] = newInstructor
    }
    
    func deleteInstructor(_ id: Int) async throws {
        try await instructorAPIService.deleteInstructor(id)
        allInstructors.removeAll { $0.instructorId == id }
    }
    
    func fetchAvailableInstructors(classTypeId: Int?, date: String, time: String) async throws -> [Int] {
        return try await instructorAPIService.findAvailableInstructors(classTypeId: classTypeId, date: date, time: time)
    }
    
    func fetchInstructorsByName(name: String?, surname: String?, input: String?) async throws -> [Instructor] {
        return try await instructorAPIService.searchInstructors(name: name, surname: surname, input: input)
    }
}
