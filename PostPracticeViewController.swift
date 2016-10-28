//
//  PostPracticeViewController.swift
//  Yoga Buddhi
//
//  Created by Oliver Hu on 7/12/16.
//  Copyright © 2016 Yoga Buddhi Co. All rights reserved.
//

import UIKit
import FBSDKShareKit

class PostPracticeViewController: PortraitViewController {
    
    @IBOutlet weak var star0: UIButton!
    @IBOutlet weak var star1: UIButton!
    @IBOutlet weak var star2: UIButton!
    @IBOutlet weak var star3: UIButton!
    @IBOutlet weak var star4: UIButton!
    @IBOutlet weak var thanksTopSpace: NSLayoutConstraint!
    @IBOutlet weak var ratingContainerOffset: NSLayoutConstraint!
    @IBOutlet weak var menuOffset: NSLayoutConstraint!
    @IBOutlet weak var playlistOffset: NSLayoutConstraint!
    @IBOutlet weak var facebookOffset: NSLayoutConstraint!
    @IBOutlet weak var membershipOffset: NSLayoutConstraint!
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var contentLabel: UILabel!
    @IBOutlet weak var ratingContainer: UIView!
    @IBOutlet weak var feedbackButton: UIButton!
    @IBOutlet weak var playlistButton: UIButton!
    @IBOutlet weak var facebookButton: UIButton!
    @IBOutlet weak var membershipButton: UIButton!
    @IBOutlet weak var feedbackHeight: NSLayoutConstraint!
    @IBOutlet weak var playlistHeight: NSLayoutConstraint!
    @IBOutlet weak var facebookHeight: NSLayoutConstraint!
    @IBOutlet weak var membershipHeight: NSLayoutConstraint!
    
    var model: SequenceModel!
    private var starsArray: Array<UIButton>!
    private let star = UIImage(named: "Star")!
    private let starFilled = UIImage(named: "StarFilled")!

    override func viewDidLoad() {
        super.viewDidLoad()
        let config = model.sequence.postPracticeConfig
        self.starsArray = [self.star0, self.star1, self.star2, self.star3, self.star4]
        
        if isIPhone4() || isIPhone5() {
            self.thanksTopSpace.constant = 20 // normal value is 40
            self.ratingContainerOffset.constant = -58 // normal value is -80
            self.menuOffset.constant = 79 // normal value is 110
            self.playlistOffset.constant = 0 // normal value is -4
            self.facebookOffset.constant = 0 // normal value is -4
            self.membershipOffset.constant = 0 // normal value is -4
        }
        
        if let label = config.title {
            self.titleLabel.text = label.text
            if label.fontSize > 0 {
                self.titleLabel.font = self.titleLabel.font.fontWithSize(CGFloat(label.fontSize))
            }
        } else {
            self.titleLabel.hidden = true
        }
        if let label = config.content {
            self.contentLabel.text = label.text
            if label.fontSize > 0 {
                self.contentLabel.font = self.contentLabel.font.fontWithSize(CGFloat(label.fontSize))
            }
        } else {
            self.contentLabel.hidden = true
        }
        self.ratingContainer.hidden = !config.displayRating
        if config.feedbackMessage == nil {
            self.feedbackButton.hidden = true
            self.feedbackHeight.constant = 0
            self.playlistOffset.constant = 0
        }
        if model.getSongsPlayed().isEmpty {
            self.playlistButton.hidden = true
            self.playlistHeight.constant = 0
            self.playlistOffset.constant = 0
        }
        if config.shareToFacebookUrl == nil {
            self.facebookButton.hidden = true
            self.facebookHeight.constant = 0
            self.facebookOffset.constant = 0
        }
        if !config.displayBecomeAMember {
            self.membershipButton.hidden = true
            self.membershipHeight.constant = 0
            self.membershipOffset.constant = 0
        }
    }

    @IBAction func starClicked(sender: AnyObject) {
        let rating = sender.tag + 1
        for i in 0..<self.starsArray.count {
            if i < rating {
                self.starsArray[i].setImage(starFilled, forState: .Normal)
            } else {
                self.starsArray[i].setImage(star, forState: .Normal)
            }
        }
        Network.INSTANCE.post(RecordRatingRequest(practiceId: model.sequence.practiceId, numStars: rating), completionHandler: nil)
    }
    
    @IBAction func feedbackClicked(sender: AnyObject) {
        self.performSegueWithIdentifier(String(MessageViewController), sender: nil)
    }
    
    @IBAction func playlistClicked(sender: AnyObject) {
        self.performSegueWithIdentifier(String(PlaylistViewController), sender: nil)
    }
    
    @IBAction func facebookClicked(sender: AnyObject) {
        let content = FBSDKShareLinkContent()
        content.contentURL = NSURL(string: model.sequence.postPracticeConfig.shareToFacebookUrl!)
        FBSDKShareDialog.showFromViewController(self, withContent: content, delegate: nil)
    }
    
    @IBAction func membershipClicked(sender: AnyObject) {
        self.performSegueWithIdentifier(String(MembershipViewController), sender: nil)
    }
    
    @IBAction func unwindToPostPractice(segue: UIStoryboardSegue) {}
    
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        if let controller = segue.destinationViewController as? MessageViewController {
            controller.practiceId = model.sequence.practiceId
            controller.sequenceTime = model.getTime()
            controller.message = model.sequence.postPracticeConfig.feedbackMessage
            controller.nextSegueId = String(PostPracticeViewController)
        } else if let controller = segue.destinationViewController as? PlaylistViewController {
            controller.songs = model.getSongsPlayed()
        } else if let controller = segue.destinationViewController as? MembershipViewController {
            controller.nextSegueId = String(PostPracticeViewController)
        }
    }
}
