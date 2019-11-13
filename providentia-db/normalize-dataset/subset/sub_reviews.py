import json

from tqdm import tqdm

import config
from norm import normalize_users, normalize_businesses

SUB_FILE = "./out/reviews_subset_{}.json".format(config.SUBSET_SETTINGS['PERC'])


def generate_subset(f_dir, perc):
    fr = open(f_dir, 'r')
    fw = open(SUB_FILE, 'w')
    num_lines = sum(1 for _ in fr)
    sub_num = int(perc * num_lines)

    # Load all user and business IDs
    print('[INFO] Loading all user and business IDs to validate reviews')
    n_user_f = open(normalize_users.NORM_FILE, 'r')
    n_business_f = open(normalize_businesses.NORM_FILE, 'r')
    users = set([json.loads(d)['user_id'] for d in n_user_f])
    businesses = set([json.loads(d)['business_id'] for d in n_business_f])
    print(users[10:])
    print(businesses[10:])
    n_user_f.close()
    n_business_f.close()

    print('[INFO] Continuing with review subset...')
    fr.seek(0)
    with tqdm(total=sub_num) as pbar:
        for line in fr:
            data = json.loads(line)
            if data['user_id'] in users and data['business_id'] in businesses:
                fw.write(line)
            pbar.update(1)
            if pbar.n == sub_num - 1:
                break

    fr.close()
    fw.close()
