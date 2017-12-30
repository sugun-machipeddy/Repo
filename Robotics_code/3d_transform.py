import numpy as np
import cv2

projection_matrix1 = [[ 255.55300658,   0,  267.89037514,    0],[   0, 255.55300658,  227.43954259,    0],[ 0, 0, 1,  0]]
projection_matrix2 = [[ 255.55300658,    0,         267.89037514,    6.38679707],[   0, 255.55300658,  227.43954259,   0],[ 0, 0, 1, 0]]

points4D = None
projPoints1 = [[315, 251, 183, 125, 140, 119, 167, 227, 292, 306],[382, 370, 344, 343, 388, 464, 479, 497, 445, 448]]
projPoints2 = [[420, 330, 258, 194, 211, 208, 243, 311, 402, 448],[325, 316, 291, 292, 336, 413, 427, 444, 388, 392]]

print("projpoints1\n",projPoints1)
print("projpoints2\n",projPoints2)
points4D = cv2.triangulatePoints(np.matrix(projection_matrix1), np.matrix(projection_matrix2), np.matrix(projPoints1), np.matrix(projPoints2), points4D)
print("points4D\n",points4D)
a = []
for i in range(0,10):
    for j in range(0,3):
         a.append(points4D[j][i]/points4D[3][i])
print('a=:\n',a)
x = [10]
p2 = []
p1 = []
for i in range(0,10):
     x.insert(i,a[3*i:3*(i+1)])
     
print('x=:\n',x)
y = [[  -0.0823035,   0.0156964,   -0.096289],[  -0.0623473,    0.025456,  -0.0857474],[  -0.0542972,   0.0421146,  -0.0740673],[  -0.0426075,   0.0591142,   -0.068591],
     [  -0.0452172,   0.0557583,  -0.0842518],[  -0.0518296,   0.0636558,   -0.106701],[  -0.0429916,   0.0532597,   -0.114655],[  -0.0546042,   0.0350766,   -0.122369],
     [  -0.0783283,   0.0231047,   -0.110335],[  -0.0930676,   0.0240686,   -0.109609]]


for i in range(0,10):
     x_t = x[i]
     x_tn = np.matrix(np.asarray(x_t))
     x_tt = np.matrix(np.matrix.transpose(x_tn))
     p1.append(x_tt)
     y_t = y[i]
     y_tn = np.matrix(np.asarray(y_t))
     y_tt = np.matrix(np.matrix.transpose(y_tn))
     p2.append(y_tt)

b = []
b = cv2.estimateAffine3D(np.matrix(np.asarray(p1)), np.matrix(np.asarray(p2)))
print('b=:\n', b)
