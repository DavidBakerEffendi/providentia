import csv
from math import pi, cos

NORM_FILE = './out/alarm_norm.csv'

# The estimated origin in order to get all the points inside of a Northern Sweden state
lat_origin = 56.63
lon_origin = -98.60

convergence = cos(lat_origin * (pi / 180.0))


def write_header():
    headers = ['X', 'Y', 't', 'Prio', 'TimeToAmbulanceStarts', 'OnSceneDuration', 'Transfer', 'TimeAtHospital',
               'TravelTimePatient', 'TravelTimeHospital', 'TravelTimeStation', 'Resource', 'X_DEST', 'Y_DEST', 'zone',
               'dest_zone', 'node', 'dest_node', 'resourceReadyTime', 'ID']
    with open(NORM_FILE, 'w') as f:
        writer = csv.DictWriter(f=f, fieldnames=headers)
        writer.writeheader()


def process_line(fw, line):
    alarm_line = [None] * 20

    data = csv.reader([line])

    i = 0
    for col in next(data):
        if i == 0 or i == 12:
            alarm_line[i] = convert_local_to_geo_lat(float(col))
        elif i == 1 or i == 13:
            alarm_line[i] = convert_local_to_geo_lon(float(col))
        elif i == 3:
            alarm_line[i] = col if col != '5' else 2
        else:
            alarm_line[i] = col
        i += 1

    fw.write(','.join(map(str, alarm_line)) + '\n')


# https://gis.stackexchange.com/questions/325594/formula-to-convert-xyz-cartesian-to-latitude-longitude-using-wgs84

def convert_local_to_geo_lat(x):
    return (x / 111120.0) + lat_origin


def convert_local_to_geo_lon(y):
    return (y / (convergence * 111120.0)) + lon_origin
