//
//  Constants.swift
//  Diction
//
//  Created by Harry Nelken on 6/20/16.
//
//

import Foundation

// STORYBOARD CONSTANTS
let kWordCellID = "wordCell"

// DATABASE CONSTANTS
let dict = DictionaryInterface()
let kDBName = "dictionary.db"
let kDefaultDBPath = NSBundle.mainBundle().resourcePath?.NS.stringByAppendingPathComponent(kDBName)

// Dynamically returns the database path
var appDBPath: String {
    let paths = NSSearchPathForDirectoriesInDomains(.DocumentDirectory, .UserDomainMask, true)
    let docsPath = paths[0]
    let dbPath = docsPath.NS.stringByAppendingPathComponent(kDBName)
    return dbPath
}

public extension String {
    var NS: NSString { return (self as NSString) }
}