import json
import csv

CSV_BUS = "./out/business.csv"
CSV_CAT = "./out/categories.csv"

BUS_HEADERS = ["business_id", "name", "address", "city", "state", "postal_code", "latitude", "longitude", "stars",
               "review_count"]
CAT_HEADERS = ["business_id", "category"]


# fUse = "~/tigergraph/loadingData/users.csv";
# fFnd = "~/tigergraph/loadingData/friendships.csv";
# fRev = '~/tigergraph/loadingData/reviews.csv";

def write_csv(f):
    fw_bus = csv.DictWriter(CSV_BUS, fieldnames=BUS_HEADERS)
    fw_bus.writeheader()

    fw_cat = csv.DictWriter(CSV_CAT, fieldnames=CAT_HEADERS)
    fw_cat.writeheader()

    with open(f, 'r') as sub_f:
        for l in sub_f:
            process_line(fw_bus, fw_cat, l)


def process_line(fw_bus, fw_cat, line):
    bus_line = dict()

    data = json.loads(line)
    for h in BUS_HEADERS:
        bus_line[h] = data[h]
    fw_bus.writerow(bus_line)

    for cat in bus_line["categories"]:
        cat_line = dict()
        cat_line["business_id"] = data["business_id"]
        cat_line["category"] = cat
        fw_cat.writerow(cat_line)