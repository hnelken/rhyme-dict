//
//  DefinitionCell.swift
//  Diction
//
//  Created by Harry Nelken on 7/13/16.
//
//

import UIKit

class DefinitionCell: UITableViewCell {

    @IBOutlet weak var definitionLabel: UILabel!
    @IBOutlet weak var exampleLabel: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
