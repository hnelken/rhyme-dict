//
//  WordListViewController.swift
//  Diction
//
//  Created by Harry Nelken on 6/22/16.
//
//

import UIKit

class WordListViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {

    var words: [[AnyObject]] = [[0, "No words found"]]
    
    @IBOutlet weak var wordsTable: UITableView!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return words.count
    }
    
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCellWithIdentifier(kWordCellID)
        
        let word = words[indexPath.row][1] as? String
        cell?.textLabel?.text = cleanWord(word!)
        
        return cell!
    }
    
    func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        tableView.deselectRowAtIndexPath(indexPath, animated: true)
    }

    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        if let vc = segue.destinationViewController as? DefinitionViewController {
            
            let path = wordsTable.indexPathForSelectedRow!
            vc.word = words[path.row]
        }
    }
    
    private func cleanWord(word: String) -> String {
        
        return word.stringByReplacingOccurrencesOfString("_", withString: " ").lowercaseString.capitalizedString
        
    }

}
