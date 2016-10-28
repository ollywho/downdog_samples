//
//  SongViewController.swift
//  Yoga Buddhi
//
//  Created by Oliver Hu on 7/19/16.
//  Copyright © 2016 Yoga Buddhi Co. All rights reserved.
//

import UIKit
import AVFoundation

class SongViewController: UIViewController {
    
    @IBOutlet weak var songTitle: UILabel!
    @IBOutlet weak var artist: UILabel!
    @IBOutlet weak var artwork: UIImageView!
    @IBOutlet weak var iTunesViewHeight: NSLayoutConstraint!
    @IBOutlet weak var spotifyViewHeight: NSLayoutConstraint!
    @IBOutlet weak var amazonViewHeight: NSLayoutConstraint!
    @IBOutlet weak var iTunesView: UIView!
    @IBOutlet weak var spotifyView: UIView!
    @IBOutlet weak var amazonView: UIView!
    @IBOutlet weak var previewButton: UIButton!
    
    var song: Song!
    var duringSequence: Bool!
    private var isPlaying = false
    private var player: AVPlayer?

    override func viewDidLoad() {
        super.viewDidLoad()
        self.songTitle.text = song.title
        self.artist.text = song.artist
        let path = FileSystem.INSTANCE.getArtworkDir().URLByAppendingPathComponent(song.artwork).path!
        if NSFileManager.defaultManager().fileExistsAtPath(path) {
            self.artwork.image = UIImage(contentsOfFile: path)
        } else {
            App.INSTANCE.runInBackground() {
                let data = NSData(contentsOfURL: NSURL(string: App.MANIFEST.artworkUrl + self.song.artwork)!)
                App.INSTANCE.runInMain() {
                    self.artwork.image = UIImage(data: data!)
                }
            }
        }
        
        if song.itunesUrl == nil {
            iTunesViewHeight.constant = 0
            iTunesView.hidden = true
        }
        if song.spotifyUrl == nil {
            spotifyViewHeight.constant = 0
            spotifyView.hidden = true
        }
        if song.amazonUrl == nil {
            amazonViewHeight.constant = 0
            amazonView.hidden = true
        }
        if let url = song.previewUrl where !duringSequence {
            player = AVPlayer(URL: NSURL(string: url)!)
            NSNotificationCenter.defaultCenter().addObserver(self, selector: #selector(SongViewController.playerDidFinishPlaying), name: AVPlayerItemDidPlayToEndTimeNotification, object: player!.currentItem)
        } else {
            previewButton.hidden = true
        }
    }
    
    @IBAction func cancelClicked(sender: AnyObject) {
        player?.pause()
        self.dismissViewControllerAnimated(true, completion: nil)
    }
    
    @IBAction func linkClicked(sender: AnyObject) {
        if let link = sender.tag == 1 ? song.itunesUrl : sender.tag == 2 ? song.spotifyUrl : song.amazonUrl {
            UIApplication.sharedApplication().openURL(NSURL(string: link)!)
        }
    }
    
    @IBAction func previewClicked(sender: AnyObject) {
        if isPlaying {
            player!.pause()
        } else {
            player!.play()
        }
        isPlaying = !isPlaying
        updatePreviewImageButton()
    }
    
    func playerDidFinishPlaying() {
        isPlaying = false
        updatePreviewImageButton()
        player!.seekToTime(CMTimeMake(0, 1000))
    }
    
    private func updatePreviewImageButton() {
        previewButton.setImage(UIImage(named: isPlaying ? "PreviewPauseButton" : "PreviewPlayButton"), forState: .Normal)
    }
    
    override func supportedInterfaceOrientations() -> UIInterfaceOrientationMask {
        return duringSequence! ? LandscapeViewController.MASK : PortraitViewController.MASK
    }
}
