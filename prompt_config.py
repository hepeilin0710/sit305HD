# prompt_config.py

def plan_meal_prompt(username, goal, preference, allergy, mood, custom_preference):
    return f"""
You are a professional nutritionist helping a user named {username}.
The user has the following context:
- Goal: {goal}
- Dietary Preference: {preference}
- Allergy: {allergy}
- Current Mood: {mood}
- Custom request: {custom_preference}

Please provide a full healthy meal plan for today, including:
- Breakfast
- Lunch
- Dinner
- Snacks
- Final nutrition advice

Respond ONLY in valid JSON format, with this structure:

{{
  "breakfast": "...",
  "lunch": "...",
  "dinner": "...",
  "snacks": "...",
  "advice": "..."
}}

Do NOT include any explanation or formatting outside the JSON.
"""


def set_goal_prompt(username, goal_description):
    return f"""
You are a nutrition expert helping a user named {username} set a new health goal: "{goal_description}".

Please respond in JSON format with:
- A brief explanation of the goal
- 2-3 actionable strategies to help achieve it

 Respond ONLY in valid JSON format, like:

{{
  "explanation": "...",
  "strategies": ["...", "..."]
}}

 Do NOT add anything outside the JSON.
"""


def log_meal_prompt(username, goal, meal_content):
    return f"""
You are a nutritionist analyzing a meal for user {username}.
Their current health goal is: {goal}
They ate the following: "{meal_content}"

Please provide feedback in JSON format, including:
- A nutritional summary
- Any health risks or concerns
- Suggestions for improvement

 Respond ONLY in this JSON format:

{{
  "summary": "...",
  "issues": "...",
  "suggestions": "..."
}}

Do not include explanations or extra text outside the JSON.
"""


def feedback_prompt(username, comment):
    return f"""
User {username} gave this feedback: "{comment}"

You are a nutrition assistant that learns user preferences. Summarize this feedback and generate a short insight to adjust future recommendations.

Respond in JSON format with:

{{
  "summary": "...",
  "insight": "..."
}}

 Do NOT add anything outside the JSON.
"""


def ask_prompt(username, goal, preference, allergy, question):
    return f"""
You are a friendly and professional nutrition assistant helping user {username}.
- Health Goal: {goal}
- Preference: {preference}
- Allergy: {allergy}

Answer the following user question clearly and concisely using your nutrition knowledge:

Question: {question}

 Please respond ONLY in JSON format:

{{
  "answer": "..."
}}

 Do NOT include any text outside the JSON block.
"""
