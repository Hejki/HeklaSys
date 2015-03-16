//
//  Node.swift
//  HeklaSys
//
//  Created by Hejki on 18.01.15.
//  Copyright (c) 2015 Hejki. All rights reserved.
//

import Foundation

class Node {
    var id: String
    var address: String
    var port: Int
    var updateInterval: Int
    var selfLink: NSURL
    var pinSettings: [PinSetting]
    
    init(json: JSON) {
        address = json["address"].stringValue
        port = json["port"].intValue
        updateInterval = json["updateInterval"].intValue
        selfLink = json["_links"]["self"]["href"].URL!
        id = selfLink.lastPathComponent!
        pinSettings = Array<PinSetting>()
    }
    
    func fetchSettings(completionHandler: () -> Void) {
        request(.GET, "http://localhost:9090/pinSettings/search/findByNode", parameters: ["node": id]).responseSwiftyJSON { (_, _, json, error) in
            
            self.pinSettings.removeAll(keepCapacity: true)
            for (_, setting) in json["_embedded"]["pinSettings"] {
                self.pinSettings.append(PinSetting(json: setting))
            }
            completionHandler()
        }
    }
}