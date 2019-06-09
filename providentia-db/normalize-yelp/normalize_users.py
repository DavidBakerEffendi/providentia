import os
import json
from tqdm import tqdm

YELP_FILE = 'user'
NORM_FILE = 'user_norm.json'


def normalize():
    with tqdm(total=num_lines) as pbar:
        for line in fr:
            process_line(line)
            pbar.update(1)


def process_line(line):
    user_line = dict()

    data = json.loads(line)
    user_line['user_id'] = data['user_id']
    user_line['name'] = data['name']
    user_line['review_count'] = data['review_count']
    user_line['yelping_since'] = data['yelping_since']
    user_line['useful'] = data['useful']
    user_line['funny'] = data['funny']
    user_line['cool'] = data['cool']
    user_line['friends'] = str(data['friends']).split(', ')
    user_line['fans'] = data['fans']
    user_line['average_stars'] = data['average_stars']
    user_line['compliment_hot'] = data['compliment_hot']
    user_line['compliment_more'] = data['compliment_more']
    user_line['compliment_profile'] = data['compliment_profile']
    user_line['compliment_cute'] = data['compliment_cute']
    user_line['compliment_list'] = data['compliment_list']
    user_line['compliment_note'] = data['compliment_note']
    user_line['compliment_plain'] = data['compliment_plain']
    user_line['compliment_cool'] = data['compliment_cool']
    user_line['compliment_funny'] = data['compliment_funny']
    user_line['compliment_writer'] = data['compliment_writer']
    user_line['compliment_photos'] = data['compliment_photos']

    fw.write(json.dumps(user_line) + '\n')


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
