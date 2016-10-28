//
//  CountdownView.swift
//  Yoga Buddhi
//
//  Created by Oliver Hu on 6/24/16.
//  Copyright © 2016 Yoga Buddhi Co. All rights reserved.
//

import UIKit

@IBDesignable
class CountdownView : UIView {
    
    var proportionRemaining: Double = 1

    required init?(coder: NSCoder) {
        super.init(coder: coder)
    }
    
    override init(frame: CGRect) {
        super.init(frame: frame)
    }
    
    func update(proportionRemaining: Double) {
        if self.proportionRemaining != proportionRemaining {
            self.proportionRemaining = proportionRemaining
            self.setNeedsDisplay()
        }
    }
    
    override func drawRect(rect: CGRect) {
        let context = UIGraphicsGetCurrentContext()
        let center = CGFloat(rect.width / 2.0)
        let lineWidth = CGFloat(2.0)
        let radius = CGFloat(center - lineWidth)
        let pi = CGFloat(-M_PI)
        let startAngle = CGFloat(0.5 * pi)
        let endAngle = 0.5 * pi + 2.0 * pi * CGFloat(self.proportionRemaining)
        
        CGContextSetLineWidth(context, lineWidth)
        CGContextSetStrokeColorWithColor(context,  UIColor(white: 1, alpha: 1).CGColor)
        CGContextBeginPath(context)
        CGContextAddArc(context, center, center, radius, startAngle, endAngle, 1)
        CGContextStrokePath(context)
    }
}
