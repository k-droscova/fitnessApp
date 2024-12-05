//
//  AppDependencies.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 03.12.2024.
//

import Foundation

let appDependencies = AppDependencies()

typealias HasNetworking = HasNetwork & HasUrlSessionWrapper
typealias HasAPIServices = HasClassTypeAPIService & HasFitnessClassAPIService & HasInstructorAPIService & HasRoomAPIService & HasTraineeAPIService
typealias HasManagers = HasUserManager & HasDataManagers
typealias HasDataManagers = HasClassTypeManager & HasFitnessClassManager & HasInstructorManager & HasRoomManager & HasTraineeManager
typealias HasStorage = HasUserDefaultsStoraging

final class AppDependencies {
    // MARK: - logger
    lazy var logger: LoggerServicing = OSLoggerService()
    
    // MARK: - storage
    lazy var userDefaultsStorage: UserDefaultsStoraging = UserDefaultsStorage(dependencies: self)
    
    // MARK: - networking
    lazy var network: Networking = Network(dependencies: self)
    lazy var urlSession: URLSessionWrapping = URLSessionWrapper()
    
    // MARK: - API services
    lazy var classTypeAPIService: ClassTypeAPIServicing = ClassTypeAPIService(dependencies: self)
    lazy var fitnessClassAPIService: FitnessClassAPIServicing = FitnessClassAPIService(dependencies: self)
    lazy var instructorAPIService: InstructorAPIServicing = InstructorAPIService(dependencies: self)
    lazy var roomAPIService: RoomAPIServicing = RoomAPIService(dependencies: self)
    lazy var traineeAPIService: TraineeAPIServicing = TraineeAPIService(dependencies: self)
    
    // MARK: - managers
    lazy var userManager: UserManaging = UserManager(dependencies: self)
    lazy var classTypeManager: ClassTypeManaging = ClassTypeManager(dependencies: self)
    lazy var fitnessClassManager: FitnessClassManaging = FitnessClassManager(dependencies: self)
    lazy var instructorManager: InstructorManaging = InstructorManager(dependencies: self)
    lazy var roomManager: RoomManaging = RoomManager(dependencies: self)
    lazy var traineeManager: TraineeManaging = TraineeManager(dependencies: self)
}

extension AppDependencies: HasLoggerService {}
extension AppDependencies: HasNetworking {}
extension AppDependencies: HasStorage {}
extension AppDependencies: HasManagers {}
extension AppDependencies: HasAPIServices {}
