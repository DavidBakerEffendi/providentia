import json

NORM_FILE = 'user_norm.json'


def process_line(fw, line):
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
