import matplotlib
matplotlib.use('Agg')  # Set the Matplotlib backend to 'Agg' for non-GUI use
from flask import request, jsonify, send_file
import matplotlib.pyplot as plt
import io

# Initialize the global variable that stores the list of numbers
chart_data = []

def receive_charts():
    # Declare chart_data as global to modify it
    global chart_data

    #Get data from Json
    data = request.get_json()

    # Print the received data for debugging
    print("Received data:", data)

    # Unpack the values that have arrived in JSON format
    new_values = data.get("numeric_value", [])

    # Validate the input
    if not isinstance(new_values, list):
        return jsonify({"error": "Invalid data format. 'numeric_value' should be a list."}), 400

    # Update chart_data to keep only the last 7 entries from new_values
    chart_data = new_values[-7:]  # Take the last 7 values from new_values

    # Print the processed chart data for debugging
    print("Processed chart data:", chart_data)

    # Create a figure and axis objects
    fig, ax = plt.subplots(figsize=(8, 6))

    # Create the histogram
    ax.bar(range(len(chart_data)), chart_data)

    # Set the x-axis labels
    ax.set_xticks(range(len(chart_data)))
    ax.set_xticklabels([f"Day {i+1}" for i in range(len(chart_data))])

    # Set the y-axis label
    ax.set_ylabel("Value")

    # Set the title
    ax.set_title("Weekly trend")

    # Adjust the spacing
    plt.tight_layout()

    # Save the plot to a BytesIO object
    img = io.BytesIO()
    plt.savefig(img, format='png')
    # Move to the beginning of the BytesIO buffer
    img.seek(0)
    # Close the figure to free memory
    plt.close(fig)

    # Reset the variable that stores data to build the chart
    chart_data = []

    # Return the image as a response
    return send_file(img, mimetype='image/png')