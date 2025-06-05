agent.py&main.py&prompt_config.py is backend code


# 🥗 NutriBot – AI-Powered Nutrition Assistant

NutriBot is an Android mobile application that leverages Large Language Models (LLMs) and Firebase to offer personalized nutrition guidance. It allows users to set health goals, generate meal plans, log daily meals, receive real-time feedback, and track their progress via an intuitive interface.

---

## ✨ Features

- 🔐 **User Authentication**: Register/login securely with Firebase Auth.
- 🎯 **Goal Setting**: Input health goals and receive actionable LLM-generated advice.
- 🍱 **Personalized Meal Plan**: Get dynamic meal suggestions based on goals, mood, and allergies.
- ✏️ **Meal Logging**: Record meals and get nutritional insights.
- 💬 **Chat with AI**: Ask nutrition-related questions with real-time responses.
- 📊 **Check-In Tracker**: Daily check-in with progress pie chart, trend line chart, and goal editing.
- 📈 **Progress Visualization**: Interactive MPAndroidChart visualizations (pie and line).
- 🧠 **Typing Animation & Ripple Effects**: Enhanced UX using Android animation and feedback components.
- 🔁 **Stateful Firebase Sync**: All user data persisted and retrieved in real-time from Firestore.

---

## 🛠️ Tech Stack

| Layer       | Technology                          |
|-------------|--------------------------------------|
| Frontend    | Android (Java), MPAndroidChart       |
| Backend     | Python (FastAPI), OpenAI API (LLM)   |
| Database    | Firebase Firestore                   |
| Auth        | Firebase Authentication              |
| Networking  | Retrofit2 (Android → FastAPI)        |

---

## 🧱 Project Structure

```bash
java/
└── com.example.hd
    ├── adapter/
    │   └── ChatAdapter.java         
    ├── backend/
    │   └── BackendService.java      # Backend service request encapsulation
    ├── model/                       # Data model for communicating with the backend
    │   ├── AskRequest.java
    │   ├──  AskResponse.java
    │   ├── ChatMessage.java
    │   ├──  FeedbackRequest.java
    │   ├──  FeedbackResponse.java
    │   ├──  GoalRequest.java
    │   ├──  GoalResponse.java
    │   ├──  LogMealRequest.java
    │   ├──  LogMealResponse.java
    │   ├── PlanMealRequest.java
    │   └── PlanMealResponse.java
    ├── network/
    │   ├── ApiService.java          # Retrofit Interface Definition
    │   └── RetrofitClient.java      # Retrofit Instance Configuration
    ├── MainActivity.java
    ├── HomeActivity.java
    ├── LoginActivity.java
    ├── RegisterActivity.java
    ├── SetGoalActivity.java
    ├── PlanMealActivity.java
    ├── LogMealActivity.java
    ├── FeedbackActivity.java
    ├── ChatActivity.java
    └── CheckInActivity.java        

```

## 🧩 Backend API Endpoints

| Endpoint           | Method | Description                  |
|--------------------|--------|------------------------------|
| `/set-goal`        | POST   | Accepts goal text, returns advice |
| `/plan-meal`       | POST   | Generates personalized meal plan |
| `/log-meal`        | POST   | Analyzes user's meal log |
| `/feedback`        | POST   | Records user feedback |
| `/ask`             | POST   | Answers nutrition-related questions |


## 🚀 How to Run
### 1. Clone this repo

```bash
git clone [https://github.com/your-repo/nutribot.git](https://github.com/hepeilin0710/sit305HD.git)
```

### 2. Configure Firebase
- Enable Authentication (Email/Password)
- Create Firestore database
- Download google-services.json from Firebase Console
- Place it inside the app/ directory
### 3. Configure API
In the backend main.py, set your OpenAI API Key:
```bash
os.environ["OPENAI_API_KEY"] = "sk-xxxxxx"
```
install：
- fastapi==0.109.0
- uvicorn==0.27.1
- pydantic==2.6.4
- langchain==0.1.16
- openai==1.16.1
### 4. Run the Backend
```bash
uvicorn main:app --reload
```
The backend will be accessible at: http://127.0.0.1:8000/
### 5. Run the Android App
- Open the project in Android Studio
- Connect a simulator or real device
- Click Run to start the app
### 6. Test the App
- Register a new account
- Fill in dietary preferences and allergies
- Explore features:
- Set health goals
- Generate personalized meal plans
- Log meals and receive feedback
- Chat with AI nutrition assistant
- Use the daily check-in system to track progres

## 📸 Screenshots

Here are some key interface previews of **NutriBot**:

### 🔐 Login & Register
![image](https://github.com/user-attachments/assets/8c51241c-f38c-4074-b56a-8bd6821b0baf)![image](https://github.com/user-attachments/assets/f5bcc931-4b99-4186-8078-19b605d39cd8)



- Firebase authentication integration
- Option to "stay signed in"

### 🏠 Home Page
![image](https://github.com/user-attachments/assets/151e4938-8ea3-47c9-b36c-5aeedc75e2be)![image](https://github.com/user-attachments/assets/dcd356d0-7f7f-4528-bcf2-93201461f248)



- Ripple click feedback
- Guidance for new users

### 🎯 Set Health Goals
![image](https://github.com/user-attachments/assets/0921fe35-1742-4e89-ae1b-9b681fd517ff)


- Enter a goal and receive strategy from LLM
- Word-by-word typing animation
- View & delete past goal records

### 🍽️ Meal Plan & Logging
![image](https://github.com/user-attachments/assets/c0500104-cf71-4968-8219-a677e99e5cec)![image](https://github.com/user-attachments/assets/715f02d0-efd6-42cf-9201-6eac07c847fa)


- AI-generated meal plan based on preferences, allergies, and mood
- Log what you eat and receive nutritional feedback

### 🤖 AI Chat Assistant &Feedback
![image](https://github.com/user-attachments/assets/a1530fd9-dee4-45b0-b75b-ed5e24552a8d)![image](https://github.com/user-attachments/assets/ea9127b0-9bc3-4b3d-8038-0412e8ea4e62)



- Chat with a smart nutrition assistant powered by LLM
- Supports user memory and follow-up interaction

### ✅ Daily Check-In
![image](https://github.com/user-attachments/assets/1ed2e6af-6c74-4a24-b6c6-cf6d850ab4d8)


- Daily progress tracking with pie chart & line chart
- Editable nickname and goals
- One-click sharing of progress

---

> 🎥 [**Watch Demo Video**](https://youtu.be/e3edul0sbHc) 
