import json

NORM_FILE = './out/photo_norm.json'

def process_line(fw, line):
    photo_line = dict()

    data = json.loads(line)
    photo_line['caption'] = data['caption']
    photo_line['photo_id'] = data['photo_id']
    photo_line['business_id'] = data['business_id']
    photo_line['label'] = data['label']

    fw.write(json.dumps(photo_line) + '\n')
