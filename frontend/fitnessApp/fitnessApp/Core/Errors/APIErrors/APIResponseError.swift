//
//  APIResponseError.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 04.12.2024.
//

import Foundation

protocol APIResponseErroring: Codable, Error {
    var message: String { get }
}

class APIResponseError: APIResponseErroring {
    let message: String
    
    enum CodingKeys: String, CodingKey {
        case message = "errorMessage"
    }
    
    required init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        self.message = try container.decode(String.self, forKey: .message)
    }
}
