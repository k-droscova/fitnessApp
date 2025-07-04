//
//  URLSessionWrapper.swift
//  fitnessApp
//
//  Created by Karolína Droscová on 03.12.2024.
//

import Foundation

protocol HasUrlSessionWrapper {
    var urlSession: URLSessionWrapping { get }
}

protocol URLSessionWrapping {
    func data(
        for request: URLRequest
    ) async throws -> (Data, URLResponse)
}

final class URLSessionWrapper: BaseClass, URLSessionWrapping {
    func data(
        for request: URLRequest
    ) async throws -> (Data, URLResponse) {
        try await URLSession.shared.data(for: request)
    }
}
