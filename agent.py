# agent.py

from langchain_openai import ChatOpenAI
from langchain.schema import HumanMessage
import os
#need openai api key

llm = ChatOpenAI(
    model="gpt-3.5-turbo",
    temperature=0.7
)

def run_agent(prompt: str) -> str:
    response = llm([HumanMessage(content=prompt)])
    return response.content
