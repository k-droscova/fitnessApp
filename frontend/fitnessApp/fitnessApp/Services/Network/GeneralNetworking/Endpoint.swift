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
            case .getById(let id), .delete(let id), .update(let id):
                return "/classtype/\(id)"
            case .getByName(name: let name):
                let basePath = "/classtype"
                return basePath.appendQueryItem(name: "name", value: name)
            case .get, .create:
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
            case .getAll, .create:
                return "/room"
            case .update(let id):
                return "/room/\(id)"
            case .delete(let id):
                return "/room/\(id)"
            case .getAvailable(let classTypeId, let date, let time):
                let basePath = "/room/available"
                let queryItems: [String: String?] = [
                    "classTypeId": classTypeId.map { "\($0)" },
                    "date": date,
                    "time": time
                ]
                return basePath.appendQueryItems(queryItems)
            case .search(let classTypeName):
                return "/room/search".appendQueryItems([
                    "classTypeName": classTypeName
                ])
            }
            
        case .fitnessClass(let action):
            switch action {
            case .getById(let id):
                return "/fitness-class/\(id)"
            case .getAll:
                return "/fitness-class"
            case .create:
                return "/fitness-class"
            case .update(let id):
                return "/fitness-class/\(id)"
            case .delete(let id):
                return "/fitness-class/\(id)"
            case .addTrainee(let id, let traineeId):
                return "/fitness-class/\(id)/add-trainee/\(traineeId)"
            case .deleteTrainee(let id, let traineeId):
                return "/fitness-class/\(id)/remove-trainee/\(traineeId)"
            case .getTrainees(let id):
                return "/fitness-class/\(id)/trainees"
            case .getByFilter(let date, let startTime, let endTime, let roomId):
                let queryItems: [String: String?] = [
                    "date": date,
                    "startTime": startTime,
                    "endTime": endTime,
                    "roomId": roomId.map { "\($0)" }
                ]
                return "/fitness-class".appendQueryItems(queryItems)
            }
            
        case .trainee(let action):
            switch action {
            case .getById(let id):
                return "/trainee/\(id)"
            case .getAll, .create:
                return "/trainee"
            case .update(let id):
                return "/trainee/\(id)"
            case .delete(let id):
                return "/trainee/\(id)"
            case .getByFitnessClassId(let fitnessClassId):
                return "/trainee/fitness-class/\(fitnessClassId)"
            case .search(let name):
                return "/trainee/search".appendQueryItems([
                    "input": name
                ])
            }
            
        case .instructor(let action):
            switch action {
            case .getById(let id):
                return "/instructor/\(id)"
            case .getAll, .create:
                return "/instructor"
            case .update(let id):
                return "/instructor/\(id)"
            case .delete(let id):
                return "/instructor/\(id)"
            case .getAvailable(let classTypeId, let date, let time):
                return "/instructor/available".appendQueryItems([
                    "classTypeId": classTypeId.map { "\($0)" },
                    "date": date,
                    "time": time
                ])
            case .search(let name, let surname, let input):
                let queryItems: [String: String?] = [
                    "name": name,
                    "surname": surname,
                    "input": input
                ]
                return "/instructor".appendQueryItems(queryItems)
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
        case getByName(name: String)
        case get
        case create
        case update(id: Int)
        case delete(id: Int)
        case getRooms(id: Int)
        case getInstructors(id: Int)
        case getFitnessClasses(id: Int)
    }
    
    enum RoomAction {
        case getById(id: Int)
        case getAll
        case create
        case update(id: Int)
        case delete(id: Int)
        case getAvailable(classTypeId: Int?, date: String, time: String)
        case search(input: String)
    }
    
    enum FitnessClassAction {
        case getById(id: Int)
        case getAll
        case create
        case update(id: Int)
        case delete(id: Int)
        case addTrainee(id: Int, traineeId: Int)
        case deleteTrainee(id: Int, traineeId: Int)
        case getTrainees(id: Int)
        case getByFilter(date: String?, startTime: String?, endTime: String?, roomId: Int?)
    }
    
    enum TraineeAction {
        case getById(id: Int)
        case getAll
        case create
        case update(id: Int)
        case delete(id: Int)
        case getByFitnessClassId(fitnessClassId: Int)
        case search(input: String)
    }
    
    enum InstructorAction {
        case getById(id: Int)
        case getAll
        case create
        case update(id: Int)
        case delete(id: Int)
        case getAvailable(classTypeId: Int?, date: String, time: String)
        case search(name: String?, surname: String?, input: String?)
    }
}
