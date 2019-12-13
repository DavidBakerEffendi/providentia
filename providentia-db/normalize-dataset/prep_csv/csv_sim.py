import csv

from tqdm import tqdm

CSV_TRANS = "./out/alarms_transf.csv"
CSV_SCENE = "./out/alarms_scene.csv"
CSV_PRIO = "./out/alarms_prio.csv"

SCENE_HEADERS = ["ID", "SID", "TravelTimeStation"]
TRANS_HEADERS = ["ID", "TID", "TravelTimeHospital"]
PRIO_HEADERS = ["ID", "Prio", "Description"]


def write_csv(f):
    f_trans = open(CSV_TRANS, 'w')
    f_scene = open(CSV_SCENE, 'w')
    f_prio = open(CSV_PRIO, 'w')
    fw_trans = csv.DictWriter(f_trans, fieldnames=TRANS_HEADERS)
    fw_scene = csv.DictWriter(f_scene, fieldnames=SCENE_HEADERS)
    fw_prio = csv.DictWriter(f_prio, fieldnames=PRIO_HEADERS)
    fw_trans.writeheader()
    fw_scene.writeheader()
    fw_prio.writeheader()

    num_lines = sum(1 for _ in open(f, 'r'))
    with tqdm(total=num_lines) as pbar:
        with open(f, 'r') as sim_file:
            reader = csv.DictReader(sim_file)
            for l in reader:
                if l['Transfer'] == "1":
                    process_travel(fw_trans, l)
                elif l['Transfer'] == "0":
                    process_scene(fw_scene, l)
                process_prio(fw_prio, l)
                pbar.update(1)

    f_trans.close()


def process_travel(fw_trans, line):
    output_line = dict()
    output_line["ID"] = line["ID"]
    output_line["TID"] = "T{}".format(line["ID"])
    output_line["TravelTimeHospital"] = line["TravelTimeHospital"]
    fw_trans.writerow(output_line)


def process_scene(fw_scene, line):
    output_line = dict()
    output_line["ID"] = line["ID"]
    output_line["SID"] = "S{}".format(line["ID"])
    output_line["TravelTimeStation"] = line["TravelTimeStation"]
    fw_scene.writerow(output_line)


def process_prio(fw_prio, line):
    output_line = dict()
    output_line["ID"] = line["ID"]
    output_line["Prio"] = line["Prio"]
    if line["Prio"] == "1":
        output_line["Description"] = "HIGH"
    elif line["Prio"] == "2" or line["Prio"] == "5":
        output_line["Description"] = "MODERATE"
    elif line["Prio"] == "3":
        output_line["Description"] = "LOW"
    fw_prio.writerow(output_line)
