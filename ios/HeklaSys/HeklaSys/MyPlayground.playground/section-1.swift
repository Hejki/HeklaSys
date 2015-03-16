// Playground - noun: a place where people can play

import Cocoa

var str = ""
var config = 1

for i in reverse(0..<8) {
    str += "\(config & (1 << i))"
}

str