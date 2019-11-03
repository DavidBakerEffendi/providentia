import os

from tqdm import tqdm


def normalize(file_dir, out_dir, process_line):
    fr = open(file_dir, 'r')
    fw = open(out_dir, 'w')
    num_lines = sum(1 for _ in open(file_dir, 'r'))

    with tqdm(total=num_lines) as pbar:
        for line in fr:
            process_line(fw, line)
            pbar.update(1)


def normalize_file(norm_setting_file, norm_module):
    if not os.path.isfile(norm_setting_file) is True:
        print(norm_setting_file, ' is not a file! Skipping...')
    else:
        print("Normalizing {}".format(norm_setting_file))
        normalize(file_dir=norm_setting_file,
                  out_dir=norm_module.NORM_FILE,
                  process_line=norm_module.process_line)


if __name__ == "__main__":
    import config
    from norm import normalize_businesses, normalize_reviews, normalize_users

    print("<--- Yelp Normalizer --->")
    if config.NORMALIZE_DATASET is True:
        # Normalize businesses
        if config.NORMALIZE_SETTINGS["NORMALIZE_BUS"] is True:
            print("Normalizing businesses...")
            normalize_file(norm_setting_file=config.NORMALIZE_SETTINGS["BUSINESS_FILE"],
                           norm_module=normalize_businesses)
        # Normalize reviews
        if config.NORMALIZE_SETTINGS["NORMALIZE_REV"] is True:
            print("Normalizing reviews...")
            normalize_file(norm_setting_file=config.NORMALIZE_SETTINGS["REVIEW_FILE"],
                           norm_module=normalize_reviews)
        # Normalize users
        if config.NORMALIZE_SETTINGS["NORMALIZE_USE"] is True:
            print("Normalizing users...")
            normalize_file(norm_setting_file=config.NORMALIZE_SETTINGS["USERS_FILE"],
                           norm_module=normalize_users)

    if config.PREPARE_CSV is True:
        # TODO: Prepare CSV
        pass