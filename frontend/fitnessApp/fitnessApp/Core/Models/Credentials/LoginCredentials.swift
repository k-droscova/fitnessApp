//
//  LoginCredentials.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 03.12.2024.
//

struct LoginCredentials: Codable, Equatable, Credentials {
    let username: String
    let password: String
    
    init(username: String, password: String) {
        self.username = username
        self.password = password
    }
    
    var params: [String: String] {
        [
            "user": username,
            "pswd": password
        ]
    }
}
