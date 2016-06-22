//
//  WordListViewController.swift
//  Diction
//
//  Created by Harry Nelken on 6/22/16.
//
//

import UIKit

class WordListViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {

    private var selectedWord: [AnyObject] = [0, "No word selected"]
    private var words: [[AnyObject]] = [[0, "No words found"]]
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        words = dict.getWordsForLetter("A")
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
        
        cell?.textLabel?.text = words[indexPath.row][1] as? String
        
        return cell!
    }
    
    func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        selectedWord = words[indexPath.row]
        tableView.deselectRowAtIndexPath(indexPath, animated: true)
    }

    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        if let vc = segue.destinationViewController as? DefinitionViewController {
            vc.word = selectedWord
        }
    }

}
