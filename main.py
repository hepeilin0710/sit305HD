# main.py

from fastapi import FastAPI
from pydantic import BaseModel
from mini_server.agent import run_agent
from mini_server import prompt_config as prompts
import json


app = FastAPI()

# ========== Request Models ==========
class AskRequest(BaseModel):
    uid: str
    username: str
    goal: str
    preference: str
    allergy: str
    question: str

class PlanMealRequest(BaseModel):
    uid: str
    username: str
    goal: str
    preference: str
    allergy: str
    mood: str
    custom_preference: str

class SetGoalRequest(BaseModel):
    uid: str
    username: str
    goal_description: str

class LogMealRequest(BaseModel):
    uid: str
    username: str
    goal: str
    meal_content: str

class FeedbackRequest(BaseModel):
    uid: str
    username: str
    comment: str

# ========== Endpoints ==========

@app.post("/ask")
def ask(req: AskRequest):
    prompt = prompts.ask_prompt(
        req.username, req.goal, req.preference, req.allergy, req.question
    )
    reply = run_agent(prompt)
    try:
        parsed = json.loads(reply)
    except json.JSONDecodeError:
        parsed = {"error": "LLM did not return valid JSON"}
    return {"reply": parsed.get("answer", reply)}


@app.post("/plan-meal")
def plan_meal(req: PlanMealRequest):
    prompt = prompts.plan_meal_prompt(
        req.username, req.goal, req.preference, req.allergy, req.mood, req.custom_preference
    )
    reply = run_agent(prompt)
    try:
        parsed = json.loads(reply)
    except json.JSONDecodeError:
        parsed = {"error": "LLM did not return valid JSON"}
    return parsed

@app.post("/set-goal")
def set_goal(req: SetGoalRequest):
    prompt = prompts.set_goal_prompt(req.username, req.goal_description)
    reply = run_agent(prompt)
    try:
        parsed = json.loads(reply)
    except json.JSONDecodeError:
        parsed = {"error": "LLM did not return valid JSON"}
    return {"goal_advice": parsed}

@app.post("/log-meal")
def log_meal(req: LogMealRequest):
    prompt = prompts.log_meal_prompt(req.username, req.goal, req.meal_content)
    reply = run_agent(prompt)
    try:
        parsed = json.loads(reply)
    except json.JSONDecodeError:
        parsed = {"error": "LLM did not return valid JSON"}
    return parsed

@app.post("/feedback")
def feedback(req: FeedbackRequest):
    prompt = prompts.feedback_prompt(req.username, req.comment)
    reply = run_agent(prompt)
    try:
        parsed = json.loads(reply)
    except json.JSONDecodeError:
        parsed = {"error": "LLM did not return valid JSON"}
    return parsed