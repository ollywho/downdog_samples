//
//  PlaylistViewController.swift
//  Yoga Buddhi
//
//  Created by Oliver Hu on 7/18/16.
//  Copyright © 2016 Yoga Buddhi Co. All rights reserved.
//

import UIKit

class PlaylistViewController: PortraitViewController, UITableViewDataSource, UITableViewDelegate {

    @IBOutlet weak var tableView: UITableView!
    
    var songs: Array<Song>!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.dataSource = self
        tableView.delegate = self
        tableView.tableFooterView = UIView()
    }
    
    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return songs.count
    }
    
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let song = songs[indexPath.row]
        let cell = tableView.dequeueReusableCellWithIdentifier("SongCell") as! SongTableViewCell
        cell.songTitle.text = song.title
        cell.artist.text = song.artist
        let path = FileSystem.INSTANCE.getArtworkDir().URLByAppendingPathComponent(song.artwork).path!
        if NSFileManager.defaultManager().fileExistsAtPath(path) {
            cell.artwork.image = UIImage(contentsOfFile: path)
        } else {
            App.INSTANCE.runInBackground() {
                let data = NSData(contentsOfURL: NSURL(string: App.MANIFEST.artworkUrl + song.artwork)!)
                App.INSTANCE.runInMain() {
                    cell.artwork.image = UIImage(data: data!)
                }
            }
        }
        return cell
    }
    
    func tableView(tableView: UITableView, willDisplayCell cell: UITableViewCell, forRowAtIndexPath indexPath: NSIndexPath) {
        cell.backgroundColor = UIColor.clearColor()
    }
    
    func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        self.performSegueWithIdentifier(String(SongViewController), sender: indexPath.row)
    }
    
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        if let controller = segue.destinationViewController as? SongViewController {
            controller.song = songs[sender as! Int]
            controller.duringSequence = false
        }
    }
    
    @IBAction func unwindToPlaylist(segue: UIStoryboardSegue) {}
}

class SongTableViewCell: UITableViewCell {
    
    @IBOutlet weak var songTitle: UILabel!
    @IBOutlet weak var artist: UILabel!
    @IBOutlet weak var artwork: UIImageView!
    
    override func awakeFromNib() {
        self.songTitle.backgroundColor = UIColor.clearColor()
        self.artist.backgroundColor = UIColor.clearColor()
    }
}
