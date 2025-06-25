import json
from datasets import Dataset
from transformers import (
    T5Tokenizer,
    T5ForConditionalGeneration,
    TrainingArguments,
    Trainer,
)
from pathlib import Path

# Load dataset
dataset_path = Path(__file__).parent.parent / "datasets/workflow_examples_100.json"
with open(dataset_path) as f:
    raw_data = json.load(f)

dataset = Dataset.from_list([{"input": x["instruction"], "output": x["workflow"]} for x in raw_data])

# Load tokenizer
model_name = "google/flan-t5-small"
tokenizer = T5Tokenizer.from_pretrained(model_name)

def preprocess(example):
    model_input = tokenizer(
        example["input"],
        truncation=True,
        padding="max_length",
        max_length=128
    )
    labels = tokenizer(
        example["output"],
        truncation=True,
        padding="max_length",
        max_length=128
    )
    model_input["labels"] = labels["input_ids"]
    return model_input

tokenized = dataset.map(preprocess)

# Load model
model = T5ForConditionalGeneration.from_pretrained(model_name)

# Training settings
args = TrainingArguments(
    output_dir="./flan-m1-model",
    per_device_train_batch_size=4,
    num_train_epochs=5,
    logging_dir="./logs",
    logging_steps=10,
    save_total_limit=1,
    save_strategy="epoch"
)

# Trainer
trainer = Trainer(
    model=model,
    args=args,
    train_dataset=tokenized
)

# Train
trainer.train()

# Save
model.save_pretrained("flan-m1-model")
tokenizer.save_pretrained("flan-m1-model")
print("Training complete. Model saved in ./flan-m1-model/")
