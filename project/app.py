from flask import Flask, request, jsonify
import pandas as pd
from sklearn.tree import DecisionTreeClassifier

app = Flask(__name__)

# Load and preprocess dataset (sample dataset)
data = pd.read_csv(r'C:\Users\ABHIRAMI\Downloads\dress_data.csv')  # Use raw string (r) for Windows paths
  # Replace this with your own dataset

# Assuming dataset has columns 'Place', 'Gender', 'Temperature', 'Weather Condition', and 'Recommendation'
# Example dataset might look like:
# | Place  | Gender | Temperature | Weather Condition | Recommendation   |
# |--------|--------|-------------|-------------------|------------------|
# | New York | Male  | 15          | Rainy             | Raincoat & Boots |
# | Mumbai   | Female| 30          | Sunny             | Cotton Dress & Sandals |
# Example preprocessing:
X = data[['Place', 'Gender', 'Temperature', 'Weather Condition']]
y = data['Recommendation']

# Convert categorical variables (Place, Gender, Weather Condition) into dummy variables
X = pd.get_dummies(X, columns=['Place', 'Gender', 'Weather Condition'])

# Train a Decision Tree model
model = DecisionTreeClassifier()
model.fit(X, y)

@app.route('/recommend', methods=['POST'])
def recommend():
    try:
        # Extract input data from the request
        input_data = request.json
        place = input_data['place']
        gender = input_data['gender']
        temperature = input_data['temperature']
        weather = input_data['condition']
        
        # Convert input data into a format that the model can process
        # Create a DataFrame with the same columns as the training data
        input_df = pd.DataFrame({'Place_' + place: [1], 'Gender_' + gender: [1], 
                                 'Temperature': [temperature], 'Weather Condition_' + weather: [1]})

        # Add missing columns in the input data to match the model's training columns
        for col in X.columns:
            if col not in input_df.columns:
                input_df[col] = 0

        # Reorder the columns to match the order in X
        input_df = input_df[X.columns]

        # Make prediction using the trained model
        recommendation = model.predict(input_df)[0]

        # Return the recommendation as JSON
        return jsonify({'recommendation': recommendation})
    except Exception as e:
        return jsonify({'error': str(e)})

if __name__ == '__main__':
    app.run(debug=True, host="0.0.0.0", port=5001)

