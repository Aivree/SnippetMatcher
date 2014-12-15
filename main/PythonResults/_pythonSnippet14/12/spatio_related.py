"""
This is to prepare the data for Wei Liu's project
Author: Jinjun Sun
Date: 2013-02-25
"""
import numpy as np
import scipy as scp
import pandas as pd
# import matplotlib
# matplotlib.use('Qt4Agg')
# matplotlib.rcParams['backend.qt4'] = 'PySide'
import pylab as pl
import os.path as osp
import pickle
#from sklearn.cluster import AffinityPropagation
from operator import itemgetter
from sklearn.metrics.pairwise import euclidean_distances


def lambert2gda(x,y):
    """
    GDA94 / NSW Lambert is a projected CRS last revised on 08/18/2005 
    and is suitable for use in Australia - New South Wales (NSW). 
    GDA94 / NSW Lambert uses the GDA94 geographic 2D CRS as its base CRS and 
    the New South Wales Lambert (Lambert Conic Conformal (2SP)) as its projection. 
    GDA94 / NSW Lambert is a CRS for Natural Resources mapping of whole State. 
    It should be very precise because it is used for the department of lands, 
    every inch of lands means a lot money in NSW. 
    calculation http://www.linz.govt.nz/geodetic/conversion-coordinates/projection-conversions/lambert-conformal-conic/index.aspx       
    """
    def m(ee,phy):
        return np.cos(phy)/np.sqrt(1 - ee**2*(np.sin(phy))**2)

    def t(ee, phy):
        return np.tan(np.pi/4. - (phy/2.)) * ((1+ee*np.sin(phy))/(1-ee*np.sin(phy)))**(ee/2.)
    
    def rho(a, F, t, n):
        return a*F*t**n
    N0 = 4500000. #unit in meter
    E0 = 9300000. # unit in meter
    Phy1 = -30.75/180. * np.pi
    Phy2 = -35.75/180. * np.pi
    Phy0 = -33.25/180. * np.pi # Phy0 is the same as Lat0?
    LatO = -33.25/180. * np.pi
    LonO = 147.0/180. * np.pi
    a = 6378137.0 # unit meter semi-major axis of reference ellipsoid
    flatten = 298.2572221101
    ee = np.sqrt(2/flatten - 1/flatten**2.)
    n = (np.log(m(ee,Phy1)) - np.log(m(ee, Phy2)))/(np.log(t(ee, Phy1))-np.log(t(ee, Phy2)))
    F = m(ee, Phy1) / n / (t(ee, Phy1)**n)
    Np = y - N0
    Ep = x - E0
    rho0 = rho(a, F, t(ee, Phy0), n)
    rhop = np.sign(n) * np.sqrt(Ep**2. + (rho0 - Np)**2.)
    tp = (rhop/a/F)**(1/n)
    rp = np.arctan(Ep/(rho0 - Np))
    longtitude = rp/n + LonO
    inLat = np.pi/2. - 2*np.arctan(tp) # initial latitude
    for i in range(10):
        llat = np.pi/2. - 2*np.arctan(tp*((1-ee*np.sin(inLat))/(1+ee*np.sin(inLat)))**(ee/2.))
        inLat = llat
    return longtitude*180./np.pi, llat*180./np.pi
#################################################################################

def genkml(df, savefile):
    #incLoc = geoplot(df,threshold)
    #incLoc = sorted(incLoc, key=lambda x: x[2], reverse=True)
    #print incLoc[0:5]
#    dfxygroups = df.groupby(['incident_location_x','incident_location_y'])
    #incLoc = zip(np.array(df.incident_location_x, dtype=float), np.array(df.incident_location_y,dtype=float))
#    incLoc = dfxygroups.groups.keys()
    incLoc = zip(df.locx, df.locy)   
    inclocgda = []
    incidentseq = df.e_id
    for (ind, (x,y)) in enumerate(incLoc):
        if x and y:
            inclocgda.append(lambert2gda(x,y)) #remove the incident times infor here kk
#            incidentcounts.append(len(dfxygroups.groups[(x,y)]))
            

    print "incidents %g" % len(inclocgda)
    #p.figure()
    fd = open(savefile, 'wb')
    beginning = """<?xml version="1.0" encoding="UTF-8"?> \n <kml xmlns="http://www.opengis.net/kml/2.2"> \n <Document> \n"""
    fd.write(beginning)
    outdata = zip(inclocgda, incidentseq)
    
    for ((xx,yy), kk) in outdata:
        #longtitude and latitude is different from Lambert projection order
        fd.write("<Placemark>\n <name> %s </name>\n" % str(kk))
        fd.write("<description> %s </description>\n" % str(kk))        
        fd.write("<Point><coordinates>%f,%f,0</coordinates>\n</Point>\n"% (yy, xx))
        fd.write("</Placemark>\n")
        #fd.write("new google.maps.LatLng(%f,%f),\n"%(xx,yy)) #This is for google map api javascript code
    fd.write("</Document></kml>")
    fd.close()
    return len(inclocgda)


def checkcorrelation(df, inds):
    """
    Return incident_ids for the pairs with time overlap
    """
    A = df.ix[inds].sort(columns='incident_start') #the index will not change
    A.index = range(len(A))
    print A.ix[1]['incident_start']
#    print A[range(5)]['incident_start'] #correct!!!
#    B = A.incident_start[1:] - A.incident_start[0:-1]    
#    C = A.incident_duration[:-1]-B
#    print " correlated cases %g \n"%np.sum(C >= 0)
    #idoi = [] # incident_id of interest
    #The pairs
    idoi1 = []
    idoi2 = []
    i1x = []
    i1y = []
    i2x = []
    i2y = []
    distance = []
    dtt = []
    durt = []
    for i in range(len(A)-1):
        dt = A.ix[i+1]['incident_start']-A.ix[i]['incident_start']
        dur = A.ix[i]['incident_duration']
        if dt <= dur:
#            print A.ix[i+1]['incident_start'], A.ix[i]['incident_start']
#            print A.ix[i]['incident_id']
#            print "Delta t %f,  duration %f"%(dt, dur)
#            print int(A.ix[i]['incident_id']), int(A.ix[i+1]['incident_id'])
            dd = np.sqrt((A.ix[i]['incident_location_x'] -
                          A.ix[i+1]['incident_location_x'])**2 +
                          (A.ix[i]['incident_location_y']-
                           A.ix[i+1]['incident_location_y'])**2)
            
            idoi1.append(A.ix[i]['incident_id']), 
            idoi2.append(A.ix[i+1]['incident_id'])
            i1x.append(A.ix[i]['incident_location_x'])
            i1y.append(A.ix[i]['incident_location_y'])
            i2x.append(A.ix[i+1]['incident_location_x'])
            i2y.append(A.ix[i+1]['incident_location_y'])
            dtt.append(dt)
            durt.append(dur)
            distance.append(dd)
    return pd.DataFrame({'id0':idoi1, 'id1':idoi2,
                         'id0x':i1x, 'id0y':i2y,
                         'id1x':i2x, 'id1y':i2y, 
                         'distance':distance,
                         'DeltaT':dtt, 'duration0':durt})

def main():
    """
    1. Do affinity propagation on the x, y !!! Failed To big to do -- 2013-02-25
    1'. try the groupby(x,y), give all 0 case of the correlation
    2. kmeans to cluster: 
    3. road name group try M4 first
    """
    csvfile = u'/home/jisun/workspace/pycode/DSIMproj/data/spatioTemporal/netLocTime.csv'
    df0 = pd.read_csv(csvfile)
#    genkml(df0)
#    dfxygroups = df0.groupby(['incident_location_x','incident_location_y'])
#    inclocs = []
#    incidentcounts = []
#    
#    for (x,y) in dfxygroups.groups.keys():
#        if x and y:
#            inclocs.append((x,y))
#            incidentcounts.append(len(dfxygroups.groups[(x,y)]))
#    print "All together %g locations"%len(inclocs)
#    outdata = sorted(zip(inclocs, incidentcounts), key=itemgetter(1), reverse=True)
#    for i in outdata:
#        if checkcorrelation(df0, dfxygroups.groups[i[0]]):
#            print "Got them ====> : ", i
    allrows(df0)

def allrows(df):
    """
    Lambert unit is meter.
    """
    nonempty = np.logical_and(df.incident_start.notnull(), df.incident_location_x.notnull())
    result = checkcorrelation(df, nonempty)
    result.to_csv('/home/jisun/workspace/pycode/DSIMproj/data/spatioTemporal/netLocRelated_address1.csv')
    result.distance.hist(bins=1000)
    pl.show()
    
    #checkcorrelation(df, nonempty)
    
    
#    outpath = u'/home/jisun/workspace/pycode/DSIMproj/data/spatioTemporal/'
#    outfile = outpath+'locnetTimeRelated.kml'
#    #genkml(A[A.incident_location_x.notnull()],outfile)
#    Dx = np.asarray(A[A.incident_location_x.notnull()]['incident_location_x'])
#    Dy = np.asarray(A[A.incident_location_x.notnull()]['incident_location_y'])
#    DD = np.sqrt(np.square(Dx[1:]-Dx[:-1]) + np.square(Dy[1:]-Dy[:-1]))
#    print "The max distance is %f"%DD.max()
#    print "The minimum distance is %f"%DD.min()
#    print DD.argmin()
#    
#    pl.hist(DD,bins=1000)
#    pl.show()

def st():
    datafile = '/home/jisun/workspace/pycode/DSIMproj/data/spatioTemporal/netLocRelated_address1.csv'
    df0 = pd.read_csv(datafile)
    print df0
    df1 = df0.sort(columns='distance')
    df1.index = range(len(df0))
#    df1.to_csv(datafile[:-4]+'sorted.csv')
    df1[np.logical_and(df1.distance == 0, df1.duration0 <= 3600)].duration0.hist(bins=100)
    
    pl.title("Incident Duration for correlated pairs with distance 0 m",size=26)
    pl.xlabel("Duration (second)", size=20)
    pl.ylabel("Histogram (counts)", size=20)
    pl.show()


def roadnamegen():
    folder = u'/home/jisun/workspace/pycode/DSIMproj/data/spatioTemporal'
    csvfile = 'all_locs.data'
    locations = pd.load(osp.join(folder,csvfile))
    print len(locations)
    locindex = pd.Index(locations)
    latlon = []
    for x,y in locations:
        latlon.append(lambert2gda(x,y))
    xy_latlon = pd.DataFrame({'latlon':latlon})
    xy_latlon.index = locindex
    xy_latlon.to_csv(osp.join(folder, 'all_xy_latlon.csv'))
    xy_latlon.save(osp.join(folder, 'all_xy_latlon.data'))
    
def report_mon_site():
    folder = u'/home/jisun/workspace/pycode/DSIMproj/data/spatioTemporal'
    csvfile = u'report_mon_sites_location_SYD.csv'
    savefile = u'report_mon_sites_locs_SYD.kml'
    df = pd.read_csv(osp.join(folder, csvfile))
    genkml(df, osp.join(folder,savefile))
    
if __name__ == "__main__":
    #main()
#    st()
#    roadnamegen()
    report_mon_site()