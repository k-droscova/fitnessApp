//
//  API.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 04.12.2024.
//

import Foundation

struct APIUrl {
    static let base: String = Bundle.main.infoDictionary?["API_BASE_URL"] as! String
    static var baseURL: URL { URL(string: base)! }
}
