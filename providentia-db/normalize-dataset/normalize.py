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
    fr.close()
    fw.close()

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
    from subset import sub_businesses, sub_reviews, sub_users

    if os.path.exists("./out") is False:
        os.mkdir("./out")

    print("<--- Yelp Normalizer --->")
    if config.NORMALIZE_DATASET is True:
        print("|--- Normalizing Original Data --|")
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

    if config.GEN_SUBSET is True:
        print("|--- Generating Subset of Data --|")
        # Subset businesses
        if config.SUBSET_SETTINGS["SUB_BUS"] is True:
            print("Generating subset of businesses...")
            sub_businesses.generate_subset(f_dir=normalize_businesses.NORM_FILE,
                                           perc=config.SUBSET_SETTINGS["PERC"])

        # Subset reviews
        if config.SUBSET_SETTINGS["SUB_REV"] is True:
            print("Generating subset of reviews...")
            sub_reviews.generate_subset(f_dir=normalize_reviews.NORM_FILE,
                                        perc=config.SUBSET_SETTINGS["PERC"])

        # Subset users
        if config.SUBSET_SETTINGS["SUB_USE"] is True:
            print("Generating subset of users...")
            sub_users.generate_subset(f_dir=normalize_users.NORM_FILE,
                                        perc=config.SUBSET_SETTINGS["PERC"])

    if config.PREPARE_CSV is True:
        # TODO: Prepare CSV
        pass
