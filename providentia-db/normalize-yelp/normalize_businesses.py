import os
import json
from tqdm import tqdm

YELP_FILE = 'business'
NORM_FILE = 'business_norm.json'


def normalize():
    with tqdm(total=num_lines) as pbar:
        for line in fr:
            process_line(line)
            pbar.update(1)


def process_line(line):
    business_line = dict()

    data = json.loads(line)
    business_line['business_id'] = data['business_id']
    business_line['name'] = data['name']
    business_line['address'] = data['address']
    business_line['city'] = data['city']  # TODO: THis could be weird too. Title case remove punctuation
    business_line['state'] = str(data['state']).upper()
    business_line['postal_code'] = data['postal_code']
    business_line['latitude'] = data['latitude']
    business_line['longitude'] = data['longitude']
    business_line['stars'] = data['stars']
    business_line['review_count'] = data['review_count']
    business_line['categories'] = str(data['categories']).split(', ')
    if data['is_open'] == 0:
        business_line['is_open'] = False
    else:
        business_line['is_open'] = True

    fw.write(json.dumps(business_line) + '\n')


if __name__ == '__main__':
    print('<--- Yelp {} Normalizer --->'.format(YELP_FILE.title()))

    filDir = ''
    while not os.path.isfile(filDir):
        filDir = input('Enter file location (relative or absolute):')
        if not os.path.isfile(filDir):
            print(filDir, ' is not a file!')

    if os.path.isfile(NORM_FILE):
        os.remove(NORM_FILE)

    fr = open(filDir, 'r')
    fw = open(NORM_FILE, 'w')
    num_lines = sum(1 for line in open(filDir, 'r'))

    normalize()
