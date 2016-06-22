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
            let sql = "SELECT * FROM wn_synset"// WHERE word LIKE '\(letter)%'"
            let results = try db.executeQuery(sql, values: [])
            while results.next() {
                // Get the word info
                let id = NSNumber(int: results.intForColumn("synset_id"))
                //let wnum = NSNumber(int: results.intForColumn("w_num"))
                let text = results.stringForColumn("word")
                
                // Add it to the list
                //let word: [AnyObject] = [id, wnum, text]
                words.append([id, text])
            }
            
            db.close()
        }
        catch {
            db.close()
        }
        
        return words
    }
    
    func getDefinitionForWord(id: NSNumber) -> String {
        let db = FMDatabase(path: appDBPath)
        db.open()
        
        var definition = "No definition found for this word"
        
        // Query results
        do {
            let sql = "SELECT * FROM wn_gloss WHERE synset_id=\(id)"
            let results = try db.executeQuery(sql, values: [])
            if results.next() {
                // Get the definition
                definition = results.stringForColumn("gloss")
            }
            
            db.close()
        }
        catch {
            db.close()
        }
        
        return definition
    }
}


