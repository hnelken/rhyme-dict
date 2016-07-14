//
//  DictionaryInterface.swift
//  Diction
//
//  Created by Harry Nelken on 6/20/16.
//
//

import UIKit

class DictionaryInterface: NSObject {
    
    func getWordsForLetter(letter: String) -> [[AnyObject]] {
        let db = FMDatabase(path: appDBPath)
        db.open()
        
        var words: [[AnyObject]] = []
        
        // Query results
        do {
            let sql = "SELECT * FROM wn_synset WHERE w_num=1 AND word LIKE '\(letter)%' GROUP BY word"
            let results = try db.executeQuery(sql, values: [])
            while results.next() {
                // Get the word info
                let id = NSNumber(int: results.intForColumn("synset_id"))
                let text = results.stringForColumn("word")
                
                // Add it to the list
                words.append([id, text])
            }
            
            db.close()
        }
        catch {
            db.close()
        }
        
        return words
    }
    
    func getDefinitionsForWord(id: NSNumber) -> [String] {
        let db = FMDatabase(path: appDBPath)
        db.open()
        
        var defs: [String] = []
        
        do {
            var ids: [Int32] = []
            
            // Get all ids rerouted to this id if any
            var sql = "SELECT synset_id FROM mult_def WHERE rrt_id=\(id)"
            var results = try db.executeQuery(sql, values: [])
            while results.next() {
                // Get the definition
                ids.append(results.intForColumn("synset_id"))
            }
            ids.append(id.intValue)
            
            // Get all definitions from accumulated ids
            for i in 0...ids.count - 1 {
                sql = "SELECT gloss FROM wn_gloss WHERE synset_id=\(ids[i]) group by synset_id"
                results = try db.executeQuery(sql, values: [])
                if results.next() {
                    // Get the definition
                    defs.append(results.stringForColumn("gloss"))
                }
            }
            db.close()
        }
        catch {
            db.close()
        }
        
        return defs
    }
}


