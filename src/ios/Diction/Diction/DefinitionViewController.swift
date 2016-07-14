//
//  DefinitionViewController.swift
//  Diction
//
//  Created by Harry Nelken on 6/22/16.
//
//

import UIKit

class DefinitionViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {

    var word: [AnyObject] = [0, "No word selected"]
    var defs: [String] = []

    @IBOutlet weak var defsTable: UITableView!
    @IBOutlet weak var wordLabel: UILabel!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }
    
    override func viewWillAppear(animated: Bool) {
        wordLabel.text = word[1] as? String
        defs = dict.getDefinitionsForWord(word[0] as! NSNumber)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return defs.count
    }
    
    //func tableView(tableView: UITableView, heightForRowAtIndexPath indexPath: NSIndexPath) -> CGFloat {
    
    //    return 150
    
    //}
    
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        
        let cell = tableView.dequeueReusableCellWithIdentifier(kDefinitionCellID) as! DefinitionCell
        
        let definition = defs[indexPath.row]
        var exampleIndex = definition.endIndex
        
        let range: Range<String.Index>? = definition.rangeOfString("\"")
        if let example = range {
            exampleIndex = example.startIndex
            cell.definitionLabel.text = definition.substringToIndex(exampleIndex)
            cell.exampleLabel.text = definition.substringFromIndex(exampleIndex)
        }
        else {
            cell.definitionLabel.text = definition
            cell.exampleLabel.text = ""
        }
                
        return cell
    }

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
