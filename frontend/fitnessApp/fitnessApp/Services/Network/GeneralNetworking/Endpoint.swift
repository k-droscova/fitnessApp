//
//  Endpoint.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 04.12.2024.
//

import Foundation

protocol Endpointing {
    var path: String { get }
    var isUserTokenRequired: Bool { get }
    func headers(userToken: String?) -> [String: String]
    func urlWithPath() throws -> URL
}

enum Endpoint: Endpointing {
    case login
    case classType(ClassTypeAction)
    case room(RoomAction)
    case fitnessClass(FitnessClassAction)
    case trainee(TraineeAction)
    case instructor(InstructorAction)
    
    var path: String {
        switch self {
        case .login:
            return "/login"
            
        case .classType(let action):
            switch action {
            case .getById(let id):
                return "/classtype/\(id)"
            case .getAll, .create, .update, .delete:
                return "/classtype"
            case .getRooms(let id):
                return "/classtype/\(id)/rooms"
            case .getInstructors(let id):
                return "/classtype/\(id)/instructors"
            case .getFitnessClasses(let id):
                return "/classtype/\(id)/fitness-classes"
            }
            
        case .room(let action):
            switch action {
            case .getById(let id):
                return "/room/\(id)"
            case .getAll, .create, .update, .delete:
                return "/room"
            case .getAvailable:
                return "/room/available"
            }
            
        case .fitnessClass(let action):
            switch action {
            case .getById(let id):
                return "/fitness-class/\(id)"
            case .getAll, .create, .update, .delete:
                return "/fitness-class"
            case .addTrainee(let id, let traineeId):
                return "/fitness-class/\(id)/add-trainee/\(traineeId)"
            case .getTrainees(let id):
                return "/fitness-class/\(id)/trainees"
            }
            
        case .trainee(let action):
            switch action {
            case .getById(let id):
                return "/trainee/\(id)"
            case .getAll, .create, .update, .delete:
                return "/trainee"
            case .getByFitnessClassId(let fitnessClassId):
                return "/trainee/fitness-class/\(fitnessClassId)"
            }
            
        case .instructor(let action):
            switch action {
            case .getById(let id):
                return "/instructor/\(id)"
            case .getAll, .create, .update, .delete:
                return "/instructor"
            case .getAvailable:
                return "/instructor/available"
            }
        }
    }
    
    var isUserTokenRequired: Bool {
        switch self {
        case .login:
            return false
        default:
            return false // Might implement later
        }
    }
    
    var typeHeaders: [String: String] {
        ["Content-Type": "application/json", "accept": "application/json"]
    }
    
    func headers(userToken: String? = nil) -> [String: String] {
        var finalHeaders = typeHeaders
        if isUserTokenRequired, let token = userToken {
            finalHeaders["Authorization"] = "Bearer \(token)"
        }
        return finalHeaders
    }
    
    func urlWithPath() throws -> URL {
        let finalURLString = APIUrl.base + path
        guard let url = URL(string: finalURLString) else {
            throw BaseError(
                context: .network,
                message: "Invalid URL",
                logger: appDependencies.logger
            )
        }
        return url
    }
}

// MARK: - Nested Action Enums for Each Entity
extension Endpoint {
    enum ClassTypeAction {
        case getById(id: Int)
        case getAll
        case create
        case update
        case delete
        case getRooms(id: Int)
        case getInstructors(id: Int)
        case getFitnessClasses(id: Int)
    }
    
    enum RoomAction {
        case getById(id: Int)
        case getAll
        case create
        case update
        case delete
        case getAvailable
    }
    
    enum FitnessClassAction {
        case getById(id: Int)
        case getAll
        case create
        case update
        case delete
        case addTrainee(id: Int, traineeId: Int)
        case getTrainees(id: Int)
    }
    
    enum TraineeAction {
        case getById(id: Int)
        case getAll
        case create
        case update
        case delete
        case getByFitnessClassId(fitnessClassId: Int)
    }
    
    enum InstructorAction {
        case getById(id: Int)
        case getAll
        case create
        case update
        case delete
        case getAvailable
    }
}
