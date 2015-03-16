//
//  PinSetting.swift
//  HeklaSys
//
//  Created by Hejki on 18.01.15.
//  Copyright (c) 2015 Hejki. All rights reserved.
//

import Foundation

enum PinSettingType: String {
    case None = "NONE"
    case ReadDigital = "READ_DIGITAL"
    case ReadAnalog = "READ_ANALOG"
    case Swithc = "SWITCH"
    case Temperature = "TEMPERATURE"
    case Humidity = "HUMIDITY"
}

class PinSetting {
    var index: Int
    var pinNumber: Int
    var type: PinSettingType
    var configuration: UInt8
    
    var configMask: String {
        var str = ""
        var config = Int(self.configuration)
        
        for i in reverse(0..<8) {
            str += " \(config & (1 << i))"
        }
        return str
    }
    
    init(json: JSON) {
        index = json["pinIndex"].intValue
        pinNumber = json["pinNumber"].intValue
        configuration = json["configuration"].uInt8 ?? 0
        type = PinSettingType(rawValue: json["type"].stringValue) ?? .None
    }
    
    
}