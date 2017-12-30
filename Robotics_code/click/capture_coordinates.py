#!/usr/bin/env python

'''
camera calibration for distorted images with chess board samples
reads distorted images, calculates the calibration and write undistorted images

usage:
    calibrate.py [<image mask>]

default values:
    
    <image mask> defaults to ../points/left*.jpg
'''

# Python 2/3 compatibility
from __future__ import print_function

import numpy as np
import cv2
import tkinter
from PIL import Image, ImageTk


# built-in modules
import os

if __name__ == '__main__':
    import sys
    import getopt
    from glob import glob

    args, img_mask = getopt.getopt(sys.argv[1:], '', ['debug=', 'square_size='])
    args = dict(args)
    
    print (args)
    if not img_mask:
        img_mask = '../image_points2/image*.jpg'  # default
    else:
        img_mask = img_mask[0]
    
    img_names = glob(img_mask)
    Proj_points1 = []
    Proj_points2 = []
    
    def callbackl(event):
        click_loc = [event.x, event.y]
        print ("you clicked on", click_loc)
        Proj_points1.append(click_loc)

    def callbackr(event):
        click_loc = [event.x, event.y]
        print ("you clicked on", click_loc)
        Proj_points2.append(click_loc)

    def next_img():
        canvas.delete('all')
        cv2.destroyAllWindows()

    for pn in img_names:
        imgpn = cv2.imread(pn, 0)
        window = tkinter.Tk(className="bla")
        if imgpn is None:
            print("Failed to load", pn)
            continue
        if (pn.find("left")>= 0):
            canvas = tkinter.Canvas(window, width=imgpn.shape[0], height=imgpn.shape[1])
            canvas.pack()
            im = Image.open(pn)
            image_tk = ImageTk.PhotoImage(im)
            canvas.create_image(imgpn.shape[0]//2, imgpn.shape[1]//2, image=image_tk)
            canvas.bind("<Button-1>", callbackl)
            tkinter.Button(text='Next image', command=next_img).pack()

        else:
            canvas = tkinter.Canvas(window, width=imgpn.shape[0], height=imgpn.shape[1])
            canvas.pack()
            im = Image.open(pn)
            image_tk = ImageTk.PhotoImage(im)
            canvas.create_image(imgpn.shape[0]//2, imgpn.shape[1]//2, image=image_tk)
            canvas.bind("<Button-1>", callbackr)
            tkinter.Button(text='Next image', command=next_img).pack()
        tkinter.mainloop()
        
            
    p1 = np.asarray(Proj_points1)
    p2 = np.asarray(Proj_points2)
    print (np.matrix.transpose(p1))
    print (np.matrix.transpose(p2))

    
    cv2.destroyAllWindows()
