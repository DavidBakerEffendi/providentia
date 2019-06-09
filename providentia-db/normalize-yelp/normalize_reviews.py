import os
import json
from tqdm import tqdm

YELP_FILE = 'review'
NORM_FILE = 'review_norm.json'


def normalize():
    with tqdm(total=num_lines) as pbar:
        for line in fr:
            process_line(line)
            pbar.update(1)


def process_line(line):
    review_line = dict()

    data = json.loads(line)
    review_line['review_id'] = data['review_id']
    review_line['user_id'] = data['user_id']
    review_line['business_id'] = data['business_id']
    review_line['stars'] = data['stars']
    review_line['useful'] = data['useful']
    review_line['funny'] = data['funny']
    review_line['cool'] = data['cool']
    review_line['text'] = data['text']
    review_line['date'] = data['date']

    fw.write(json.dumps(review_line) + '\n')


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
