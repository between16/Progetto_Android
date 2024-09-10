from flask import request, jsonify, send_file
from textblob import TextBlob as tb

def receive_note():
    #Get data in json format
    data = request.get_json()
    print(data)

    # unpack the value that has arrived in json format
    note_value = data.get("note", "")

    #creating object blob from class textBlob
    blob = tb(note_value)

    #to get the value from the sentiment analysis
    sentiment = blob.sentiment.polarity

    #debug to check is value is meaningful
    print(sentiment)

    return jsonify({"sentiment": sentiment})