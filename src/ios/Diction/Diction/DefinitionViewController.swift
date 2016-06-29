//
//  DefinitionViewController.swift
//  Diction
//
//  Created by Harry Nelken on 6/22/16.
//
//

import UIKit

class DefinitionViewController: UIViewController {

    var word: [AnyObject] = [0, "No word selected"]
    @IBOutlet weak var wordLabel: UILabel!
    @IBOutlet weak var definitionLabel: UILabel!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }
    
    override func viewWillAppear(animated: Bool) {
        wordLabel.text = word[1] as? String
        let defs = dict.getDefinitionsForWord(word[0] as! NSNumber)
        definitionLabel.text = defs[0]
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
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
