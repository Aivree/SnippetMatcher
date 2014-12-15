'''
Created on Nov 5, 2013

@author: minjoon
'''
import numpy as np
import matplotlib.pyplot as plt
from scipy.ndimage.filters import gaussian_filter
import cv2
from img2graph import *
import matplotlib.cm as cm
from scipy.ndimage.filters import maximum_filter
from scipy.ndimage.morphology import generate_binary_structure, binary_erosion


def hough_lines(img, T=360, R=400,gradient=True):
    '''
    img: image pointer
    T: size of theta bins [0,pi/2]
    R: size of rho bins [0,sqrt(rowsize^2+colsize^2]
    '''
    rowsize, colsize = np.shape(img)
    MAX_RHO = np.sqrt(rowsize*rowsize+colsize*colsize)
    thetas = np.linspace(0,np.pi,T)
    rhos = np.linspace(-MAX_RHO,MAX_RHO,R)
    rtmat = np.zeros((R,T))
    fbdict = {}
    for r in range(rowsize):
        for c in range(colsize):
            if img[r,c] < 1:
                x = c
                y = r
                curr_rhos = x*np.cos(thetas)+y*np.sin(thetas)
                rho_inds = val2ind(curr_rhos, R, MAX_RHO, start=-MAX_RHO)
                fbdict[(r,c)] = rho_inds
                for ind in range(T):
                    val = 1
                    if gradient:
                        val=1-img[r,c]
                    rtmat[rho_inds[ind],ind] += (val)
    return (rtmat, thetas, rhos, fbdict)

def circles(img, minRadius, maxRadius,minDist=2,param2=50):
    cs = cv2.HoughCircles(img,cv2.cv.CV_HOUGH_GRADIENT,dp=1,minDist=minDist,param1=50,param2=param2,minRadius=minRadius,maxRadius=maxRadius)
    if cs is not None:
        cs[0][:,(0,1)] = cs[0][:,(1,0)]
        return cs[0]
    else:
        return []

def peaks(rtmat, thetas, rhos, num, eps, min_rate):
    '''
    dim = np.shape(rtmat)
    nbr = generate_binary_structure(2,2)
    local_max = maximum_filter(rtmat, footprint=nbr)==rtmat
    bg = (rtmat==0)
    eroded_bg = binary_erosion(bg,structure=nbr,border_value=1)
    rt_proc = local_max - eroded_bg
    arr_rt = rt_proc.flatten()
    array_ind_argmax = arr_rt.argsort()[-num:]
    array_pt = []
    for ind_argmax in array_ind_argmax:
        pt = np.unravel_index(ind_argmax,dim)
        array_pt.append(pt)
    return np.array(array_pt)
    '''
    
    dim = np.shape(rtmat)
    rtarr = rtmat.flatten()
    pts = []
    maxval = 0
    for ind in range(num):
        fpt = np.argmax(rtarr)
        val = rtarr[fpt]
        if ind == 0:
            maxval = val
        if val >= maxval*min_rate:
            pt = np.unravel_index(fpt, dim)
            pts.append((rhos[pt[0]], thetas[pt[1]]))
            process(rtarr, dim, fpt, eps) # e.g. set all pts within eps to 0 in rtarr
        else:
            return np.array(pts)
    return np.array(pts)

def process(rtarr, dim, fpt, eps):
    pt = np.unravel_index(fpt, dim)
    rval, tval = pt
    R, T = dim
    reps, teps = eps
    for r in np.arange(max(0,rval-reps),min(rval+reps,R)):
        for t in np.arange(max(0,tval-teps),min(tval+teps,T)):
            find = np.ravel_multi_index((r,t),dim)
            rtarr[find] = 0
            
            
def line2seg(rtpair):
    rho, theta = rtpair
    init = np.array((rho*np.cos(theta), rho*np.sin(theta)))
    delta = np.array((-np.sin(theta), np.cos(theta)))
    eta = 1000 
    pos = init+eta*delta
    neg = init-eta*delta
    return (pos, neg)
    

# np.unravel_index : flatten index to dimensional index
# np.argmax : gets argmax in flattened array
# np.ravel_multi_index: dimensional index to flatten index
# A.flatten()

def val2ind(val, numbins, end, start=0):
    binsize = float(end-start)/numbins
    return np.floor((val-start)/binsize).astype(int)

def ind2val(ind, numbins, end, start=0):
    binsize = float(end-start)/numbins
    return binsize*(ind+0.5)

def feedback(img, rtmat, fbdict):
    rowsize, colsize = np.shape(img)
    fbmat = np.zeros((rowsize,colsize))
    for r in range(rowsize):
        for c in range(colsize):
            if img[r,c] < 1:
                rho_inds = fbdict[(r,c)]
                vals = rtmat[rho_inds,range(len(rho_inds))]
                if False:
                    plt.plot(gaussian_filter(vals,3))
                    plt.show()
                fbmat[r,c] = np.count_nonzero(vals>60)
    return fbmat

def dist(x, y, r, t):
    return np.abs(r-(x*np.cos(t)+y*np.sin(t)))

def line2pts(line, allpts, eps=2): 
    pts = []
    r, t = line
    for i in range(len(allpts[0])):
        y = allpts[0][i]
        x = allpts[1][i]
        if dist(x,y,r,t) < eps:
            pts.append((y,x)) 
    return np.array(pts)

def line2segs(line, img, gap=3,minlen=20):
    allpts = np.nonzero(img<1)
    pts = line2pts(line,allpts,eps=1.5)
    # for all points, project on to the line
    rho, theta = line
    p0 = rho*np.array([np.sin(theta), np.cos(theta)])
    u = np.array([np.cos(theta), -np.sin(theta)])
    ks = np.zeros(len(pts))
    for i,(y,x) in enumerate(pts):
        p = np.array([y,x])
        k = np.dot(u,p-p0) 
        ks[i] = k
    sorted_ks = np.sort(ks)
    segs = []
    init = None
    for i,k in enumerate(sorted_ks):
        pp = p0 + k*u
        if init == None:
            init = pp
        if i + 1 == len(sorted_ks) or np.abs(sorted_ks[i]-sorted_ks[i+1]) > gap:
            seg = (init,pp)
            if np.linalg.norm(init-pp) > minlen:
                segs.append(seg)
            init = None
    return segs

def lines2segs(lines, img, gap=5,minlen=10):
    segs = []
    for line in lines:
        segs.extend(line2segs(line,img,gap=gap,minlen=minlen))
    return segs
    
def color(img, lines, eps=0.5):
    rowsize, colsize = np.shape(img)
    pts = []
    for y in range(rowsize):
        for x in range(colsize):
            if img[y,x] < 1:
                sum = 0
                for r,t in lines:
                    if dist(x,y,r,t) < eps:
                        sum += 1
                if sum > 1:
                    pts.append((y,x))
    return np.array(pts)

def harris_corners(imgmat, draw=False):
    imgmat_8 = to0_255(imgmat)
    gray = np.float32(imgmat_8)
    dst = cv2.cornerHarris(gray,2,3,0.04)
    cnrs = np.transpose(np.nonzero(dst>0.1*dst.max()))
    
    if draw:
        # Draw corners
        plt.plot(np.transpose(cnrs)[1],np.transpose(cnrs)[0],'xb', markersize=10)
        plt.show()

    return cnrs


def hough_shapes(imgmat, out, draw=False, line_n=20, line_nms=(2,2), line_min=0.1, cir_nms=1, cir_param2=50):    
    
    ysize, xsize = np.shape(imgmat)
    rtmat, thetas, rhos, fbdict = out
    
    gfrtmat = gaussian_filter(rtmat, [1,1])
    #fbmat = feedback(imgmat, rtmat, fbdict)
    lines = peaks(rtmat,thetas, rhos,line_n,line_nms,line_min)
    # lines2 = peaks(rtmat2, thetas, rhos, 10,(20,30),0.2)
    segs = lines2segs(lines,imgmat)
    # segs2 = lines2segs(lines2,imgmat)
    
    # circles
    imgmat_8 = to0_255(imgmat)
    cirs = circles(imgmat_8,30,300,minDist=cir_nms, param2=cir_param2)
    cirs = cirs[:20]
    # cirs2 = circles(imgmat_8,30,300,minDist=8)
    
    if draw:
        # plt.imshow(imgmat, cmap=cm.Greys_r)
        
        
        # draw hough space
        extent = (min(thetas),max(thetas),min(rhos),max(rhos))
        plt.imshow(rtmat, aspect='auto',origin='lower',extent=extent)
        plt.xlim(extent[:2])
        plt.ylim(extent[2:])
        plt.xlabel(r'$\theta$')
        plt.ylabel(r'$\rho$')
        plt.plot(lines[:,1],lines[:,0],'xk', markersize=10)
        plt.show()

        # draw segs
        plt.imshow(imgmat, cmap=cm.Greys_r)
        for pt1,pt2 in segs:
            xs = [pt1[1],pt2[1]]
            ys = [pt1[0],pt2[0]]
            plt.plot(xs,ys,'r')
            
        # Draw circles    
        fig = plt.gcf()
        for cir in cirs:
            row = cir[0]
            col = cir[1]
            rad = cir[2]
            cir_handle=plt.Circle((col,row),rad,color='r',fill=False)
            fig.gca().add_artist(cir_handle)
            
        plt.xlim(0,xsize)
        plt.ylim(ysize,0)
    return (segs, cirs)

                    
