//
//  DictionaryInterface.swift
//  Diction
//
//  Created by Harry Nelken on 6/20/16.
//
//

import UIKit

class DictionaryInterface: NSObject {
    
    func doStuff() {
        
        let db = FMDatabase(path: appDBPath)
        db.open()
        
        // Query results
        do {
            let sql = "SELECT * FROM wn_synset"
            let results = try db.executeQuery(sql, values: [])
            if results.next() {
                print(results.stringForColumn("word"))
            }
            
            db.close()
        }
        catch {
            db.close()
        }
        
    }
    
}


