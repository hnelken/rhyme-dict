//
//  ViewController.swift
//  Diction
//
//  Created by Harry Nelken on 6/15/16.
//
//

import UIKit

class ViewController: UIViewController {
    
    @IBOutlet weak var textLabel: UILabel!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func onButton(sender: AnyObject) {
        dict.doStuff()
        textLabel.textColor = UIColor.blackColor()
    }

    
    @IBAction func offButton(sender: AnyObject) {
        textLabel.textColor = UIColor.whiteColor()
    }
}

