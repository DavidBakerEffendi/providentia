import json
import csv
import config
from tqdm import tqdm

CSV_PHOTO = "./out/photos_{}.csv".format(config.SUBSET_SETTINGS['PERC'])

PHOTO_HEADERS = ["caption", "photo_id", "business_id", "label"]


def write_csv(f):
    f_rev = open(CSV_PHOTO, 'w')
    fw_rev = csv.DictWriter(f_rev, fieldnames=PHOTO_HEADERS)

    fw_rev.writeheader()

    num_lines = sum(1 for _ in open(f, 'r'))
    with tqdm(total=num_lines) as pbar:
        with open(f, 'r') as sub_f:
            for l in sub_f:
                process_line(fw_rev, l)
                pbar.update(1)

    f_rev.close()


def process_line(fw_rev, line):
    rev_line = dict()

    data = json.loads(line)
    for h in PHOTO_HEADERS:
        if type(data[h]) is str:
            rev_line[h] = data[h].replace(",", "")
        else:
            rev_line[h] = data[h]
    fw_rev.writerow(rev_line)