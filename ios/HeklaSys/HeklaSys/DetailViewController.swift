//
//  DetailViewController.swift
//  HeklaSys
//
//  Created by Hejki on 18.01.15.
//  Copyright (c) 2015 Hejki. All rights reserved.
//

import UIKit

class DetailViewController: UITableViewController {

    var detailItem: Node? {
        didSet {
            configureView()
        }
    }

    func configureView() {
        if let detail = detailItem {
            detail.fetchSettings {
                self.tableView.reloadData()
            }
            tableView.reloadData()
        }
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        configureView()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    // MARK: - TableView
    override func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return detailItem != nil ? Sections.Count.rawValue : 0
    }
    
    override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if let section = Sections(rawValue: section) {
            return section.numOfRows(detailItem!)
        }
        return 0
    }

    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let section = Sections(rawValue: indexPath.section)!
        return section.cell(tableView, row: indexPath.row, node: detailItem!)
    }
    
    override func tableView(tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        let section = Sections(rawValue: section)!
        return section.title
    }
    
    // MARK: - Actions
    @IBAction func showActions() {
        
    }
    
    @IBAction func switchToEditMode() {
        
    }
}

// MARK: - Private
private extension DetailViewController {
    enum Sections: Int {
        case NodeDetail = 0
        case PinSettings
        case Count
        
        var title: String {
            get {
                switch self {
                case .NodeDetail:
                    return "Node"
                case .PinSettings:
                    return "Pin Settings"
                default:
                    return ""
                }
            }
        }
        
        func numOfRows(node: Node) -> Int {
            switch self {
            case .NodeDetail:
                return 3
            case .PinSettings:
                return node.pinSettings.count + 1
            default:
                return 0
            }
        }
        
        func cell(tableView: UITableView, row: Int, node: Node) -> UITableViewCell {
            if self == .NodeDetail {
                let cell = tableView.dequeueReusableCellWithIdentifier("TitledCell") as UITableViewCell
                
                switch row {
                case 0:
                    cell.textLabel?.text = "Address"
                    cell.detailTextLabel?.text = node.address
                case 1:
                    cell.textLabel?.text = "Port"
                    cell.detailTextLabel?.text = node.port.description
                case 2:
                    cell.textLabel?.text = "Update interval"
                    cell.detailTextLabel?.text = node.updateInterval.description
                default:
                    cell.textLabel?.text = ""
                    cell.detailTextLabel?.text = ""
                }
                
                return cell
            }
            
            if self == .PinSettings {
                if row == 0 {
                    return tableView.dequeueReusableCellWithIdentifier("PinSettingsHeaderCell") as UITableViewCell
                }
                
                let cell = tableView.dequeueReusableCellWithIdentifier("PinSettingsCell") as PinSettingTableViewCell
                let setting = node.pinSettings[row - 1]
                
                cell.indexLabel.text = setting.index.description
                cell.typeLabel.text = setting.type.rawValue
                cell.pinNumberLabel.text = setting.pinNumber.description
                cell.configLabel.text = setting.configMask
                return cell;
            }
            
            return UITableViewCell()
        }
    }
}

class PinSettingTableViewCell: UITableViewCell {
    @IBOutlet weak var indexLabel: UILabel!
    @IBOutlet weak var typeLabel: UILabel!
    @IBOutlet weak var pinNumberLabel: UILabel!
    @IBOutlet weak var configLabel: UILabel!
}