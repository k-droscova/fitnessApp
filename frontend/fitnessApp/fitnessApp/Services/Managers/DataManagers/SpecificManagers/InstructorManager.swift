//
//  InstructorManager.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 04.12.2024.
//

import Foundation

protocol InstructorManagerFlowDelegate: NSObject {
    func onInstructorCreated()
    func onInstructorUpdated()
    func onInstructorDeleted()
    func onError(_ error: Error)
}

protocol HasInstructorManager {
    var instructorManager: any InstructorManaging { get }
}

protocol InstructorManaging {
    var delegate: InstructorManagerFlowDelegate? { get set }
    var allInstructors: [Instructor] { get }
    func fetchInstructors() async
    func fetchInstructorById(_ id: Int) async -> Instructor?
    func createInstructor(_ instructor: Instructor) async
    func updateInstructor(_ id: Int, _ newInstructor: Instructor) async
    func deleteInstructor(_ id: Int) async
    func findAvailableInstructors(classTypeId: Int?, date: String, time: String) async -> [Int]
    func searchInstructors(name: String?, surname: String?, input: String?) async -> [Instructor]
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
    
    // MARK: - Public Interface
    
    func fetchInstructors() async {
        do {
            let result = try await instructorAPIService.allInstructors()
            allInstructors = result
        } catch {
            delegate?.onError(error)
        }
    }
    
    func fetchInstructorById(_ id: Int) async -> Instructor? {
        do {
            return try await instructorAPIService.instructor(id)
        } catch {
            delegate?.onError(error)
            return nil
        }
    }
    
    func createInstructor(_ instructor: Instructor) async {
        do {
            try await instructorAPIService.postNewInstructor(instructor)
            allInstructors.append(instructor)
            delegate?.onInstructorCreated()
        } catch {
            delegate?.onError(error)
        }
    }
    
    func updateInstructor(_ id: Int, _ newInstructor: Instructor) async {
        do {
            try await instructorAPIService.updateInstructor(id, newInstructor)
            guard let index = allInstructors.firstIndex(where: { $0.id == id }) else {
                throw BaseError(
                    context: .system,
                    message: "Instructor with id \(id) not found in local cache",
                    logger: self.logger
                )
            }
            allInstructors[index] = newInstructor
            delegate?.onInstructorUpdated()
        } catch {
            delegate?.onError(error)
        }
    }
    
    func deleteInstructor(_ id: Int) async {
        do {
            try await instructorAPIService.deleteInstructor(id)
            allInstructors.removeAll { $0.id == id }
            delegate?.onInstructorDeleted()
        } catch {
            delegate?.onError(error)
        }
    }
    
    func findAvailableInstructors(classTypeId: Int?, date: String, time: String) async -> [Int] {
        do {
            return try await instructorAPIService.findAvailableInstructors(
                classTypeId: classTypeId,
                date: date,
                time: time
            )
        } catch {
            delegate?.onError(error)
            return []
        }
    }
    
    func searchInstructors(name: String?, surname: String?, input: String?) async -> [Instructor] {
        do {
            return try await instructorAPIService.searchInstructors(
                name: name,
                surname: surname,
                input: input
            )
        } catch {
            delegate?.onError(error)
            return []
        }
    }
}
