#!/usr/bin/env python


# Python 2/3 compatibility
from __future__ import print_function
from __future__ import division

import numpy as np
import cv2

# local modules
from common import splitfn

# built-in modules
import os

if __name__ == '__main__':
    import sys
    import getopt
    from glob import glob

    args, img_mask = getopt.getopt(sys.argv[1:], '', ['debug=', 'square_size='])
    args = dict(args)
    args.setdefault('--debug', './output/')
    args.setdefault('--square_size', 0.026)
    print (args)
    if not img_mask:
        img_mask = '../data/image*.jpg'  # default
    else:
        img_mask = img_mask[0]
    # print (img_mask)
    img_names = glob(img_mask)
    debug_dir = args.get('--debug')
    if not os.path.isdir(debug_dir):
        os.mkdir(debug_dir)
    square_size = float(args.get('--square_size'))

    pattern_size = (6, 8)
    pattern_points = np.zeros((np.prod(pattern_size), 3), np.float32)
    pattern_points[:, :2] = np.indices(pattern_size).T.reshape(-1, 2)
    pattern_points *= square_size

    obj_points = []
    img_points1 = []
    img_points2 = []
    h, w = 0, 0
    img_names_undistort = []
    for fn in img_names:
        img = cv2.imread(fn, 0)
        if img is None:
            print("Failed to load", fn)
            continue

        h, w = img.shape[:2]
        print (h, w)
        print(img_names)

        if (fn.find("left")>= 0):
            print('processing %s... ' % fn, end='')
            
            found, corners = cv2.findChessboardCorners(img, pattern_size)
            if found:
                term = (cv2.TERM_CRITERIA_EPS + cv2.TERM_CRITERIA_COUNT, 30, 0.1)
                print (term)
                print (cv2.TERM_CRITERIA_COUNT)
                cv2.cornerSubPix(img, corners, (5, 5), (-1, -1), term)

            if debug_dir:
                vis = cv2.cvtColor(img, cv2.COLOR_GRAY2BGR)
                cv2.drawChessboardCorners(vis, pattern_size, corners, found)
                path, name, ext = splitfn(fn)
                outfile = debug_dir + name + '_chess.png'
                cv2.imwrite(outfile, vis)
            if found:
                img_names_undistort.append(outfile)

            if not found:
                print('chessboard not found')
                continue

            img_points1.append(corners.reshape(-1, 2))
            obj_points.append(pattern_points)

            print('ok')

        else:

            print('processing %s... ' % fn, end='')
        
            found, corners = cv2.findChessboardCorners(img, pattern_size)
            if found:
                term = (cv2.TERM_CRITERIA_EPS + cv2.TERM_CRITERIA_COUNT, 30, 0.1)
                print (term)
                print (cv2.TERM_CRITERIA_COUNT)
                cv2.cornerSubPix(img, corners, (5, 5), (-1, -1), term)

            if debug_dir:
                vis = cv2.cvtColor(img, cv2.COLOR_GRAY2BGR)
                cv2.drawChessboardCorners(vis, pattern_size, corners, found)
                path, name, ext = splitfn(fn)
                outfile = debug_dir + name + '_chess.png'
                cv2.imwrite(outfile, vis)
            if found:
                img_names_undistort.append(outfile)

            if not found:
                print('chessboard not found')
                continue

            img_points2.append(corners.reshape(-1, 2))
            obj_points.append(pattern_points)
            

    # calculate camera distortion
    
    cameraMatrix1 =[[ 256.17012132,   0,        331.67953468],
    [   0,        255.94880939,  279.05925333],
    [   0,           0,          1       ]]
   
    distCoeffs1 = [ 0.01904914, -0.0297657,  -0.00186113, -0.00266531,  0.00172731]
   
    cameraMatrix2 = [[ 255.88403329,    0,         336.44785266],
    [   0,         255.55300658,  224.10856234],
    [   0,            0,           1,     ]]
    
    distCoeffs2 = [ 0.01497624, -0.01792683, -0.0034088,  -0.00360699, -0.00985457]

    R = None
    T = None
    E = None
    F = None
    
    calibration_output = []
    
    
    retval, camera_matrix1, dist_coefs1, camera_matrix2, dist_coefs2, R, T, E, F = cv2.stereoCalibrate(obj_points, img_points1, img_points2, np.matrix(cameraMatrix1), np.matrix(distCoeffs1), np.matrix(cameraMatrix2), np.matrix(distCoeffs2), (w, h), R, T, E= None, F=None)

    print("camera matrix1:\n", camera_matrix1)
    print("distortion coefficients1: ", dist_coefs1.ravel())
    print("camera matrix2:\n", camera_matrix2)
    print("distortion coefficients2: ", dist_coefs2.ravel())
    print("R:\n", R)
    print("T:\n",T)
    
    
    rectify_output = []
    R1= None
    R2=None
    P1=None
    P2=None
    Q=None
    rectify_output = cv2.stereoRectify(np.matrix(camera_matrix1), np.matrix(dist_coefs1), np.matrix(camera_matrix2), np.matrix(dist_coefs2), (w, h), R, T, R1, R2, P1, P2, Q=None)
    print("projection matrix1:\n", rectify_output[2])
    print("projection matrix2:\n", rectify_output[3])
    
    cv2.destroyAllWindows()
