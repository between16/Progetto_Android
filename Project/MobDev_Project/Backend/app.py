from flask import Flask, request, jsonify, send_file
from flask_cors import CORS
from chart import receive_charts
from nlp import receive_note

#to initiate the flask application
app = Flask(__name__)
# to accept all requests from any source
cors = CORS(app, origins="*")

@app.route("/")
def hello():
    return "Hello World!"

# Receive data from client in order to make the charts
@app.route("/api/receive_charts", methods=["POST"])
def receive_charts_route():
    return receive_charts()

#receive the text note in order to perform the sentiment analysis
@app.route("/api/sentiment_analysis", methods=["POST"])
def senstiment_analysis():
    return receive_note()

if __name__ == "__main__":
    app.run(debug=True)