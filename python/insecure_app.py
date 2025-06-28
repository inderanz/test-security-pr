#!/usr/bin/env python3
"""
SECURITY ISSUES: Insecure Python application
"""

import os
import sqlite3
import subprocess
import hashlib
import base64
import pickle
from flask import Flask, request, jsonify

app = Flask(__name__)

# SECURITY ISSUE: Hardcoded database credentials
DB_PATH = "/var/lib/app/database.db"
DB_USER = "admin"
DB_PASSWORD = "super_secret_password123"  # Should use environment variables

# SECURITY ISSUE: Hardcoded API key
STRIPE_API_KEY = "sk_test_1234567890abcdef"  # Should be in config

# SECURITY ISSUE: Weak encryption key
ENCRYPTION_KEY = "mysecretkey123"  # Should be 256-bit key

@app.route('/process-payment', methods=['POST'])
def process_payment():
    """SECURITY ISSUE: SQL Injection vulnerability"""
    try:
        card_number = request.form.get('card_number')
        amount = request.form.get('amount')
        user_id = request.form.get('user_id')
        
        # SECURITY ISSUE: No input validation
        # SECURITY ISSUE: SQL Injection - direct string concatenation
        sql = f"INSERT INTO payments (card_number, amount, user_id) VALUES ('{card_number}', {amount}, '{user_id}')"
        
        conn = sqlite3.connect(DB_PATH)
        cursor = conn.cursor()
        cursor.execute(sql)
        conn.commit()
        conn.close()
        
        # SECURITY ISSUE: Logging sensitive data
        print(f"Payment processed for card: {card_number}")
        
        return jsonify({"status": "success"})
    except Exception as e:
        # SECURITY ISSUE: No proper error handling
        print(f"Error: {e}")
        return jsonify({"status": "error"})

@app.route('/execute', methods=['POST'])
def execute_command():
    """SECURITY ISSUE: Command injection vulnerability"""
    try:
        command = request.form.get('command')
        
        # SECURITY ISSUE: Command injection
        result = subprocess.run(command, shell=True, capture_output=True, text=True)
        
        return jsonify({
            "stdout": result.stdout,
            "stderr": result.stderr,
            "returncode": result.returncode
        })
    except Exception as e:
        return jsonify({"error": str(e)})

@app.route('/upload', methods=['POST'])
def upload_file():
    """SECURITY ISSUE: Path traversal vulnerability"""
    try:
        file = request.files['file']
        filename = request.form.get('filename')
        
        # SECURITY ISSUE: Path traversal vulnerability
        upload_path = f"/uploads/{filename}"  # Should validate filename
        
        # SECURITY ISSUE: No file type validation
        # SECURITY ISSUE: No file size limits
        
        file.save(upload_path)
        
        # SECURITY ISSUE: Logging file path
        print(f"File uploaded to: {upload_path}")
        
        return jsonify({"status": "success"})
    except Exception as e:
        return jsonify({"error": str(e)})

def hash_password(password):
    """SECURITY ISSUE: Weak password hashing"""
    try:
        # SECURITY ISSUE: Using weak hash algorithm
        hash_object = hashlib.md5(password.encode())  # Should use bcrypt/argon2
        return hash_object.hexdigest()
    except Exception as e:
        # SECURITY ISSUE: Fallback to plain text
        return password

def encrypt_data(data):
    """SECURITY ISSUE: Weak encryption implementation"""
    try:
        # SECURITY ISSUE: Using weak encryption
        # This is a simplified example - real implementation would be more complex
        encoded = base64.b64encode(data.encode())
        return encoded.decode()
    except Exception as e:
        # SECURITY ISSUE: No proper error handling
        return data

@app.route('/process-data', methods=['POST'])
def process_data():
    """SECURITY ISSUE: Insecure deserialization"""
    try:
        data = request.get_data()
        
        # SECURITY ISSUE: Insecure deserialization
        obj = pickle.loads(data)
        
        return jsonify({"result": str(obj)})
    except Exception as e:
        return jsonify({"error": str(e)})

@app.route('/authenticate', methods=['POST'])
def authenticate():
    """SECURITY ISSUE: Weak authentication"""
    try:
        username = request.form.get('username')
        password = request.form.get('password')
        
        # SECURITY ISSUE: No input validation
        # SECURITY ISSUE: No rate limiting
        # SECURITY ISSUE: No account lockout
        
        # SECURITY ISSUE: Weak authentication logic
        if username == "admin" and password == "password123":
            return jsonify({"status": "authenticated"})
        else:
            return jsonify({"status": "failed"})
    except Exception as e:
        return jsonify({"error": str(e)})

@app.route('/config', methods=['GET'])
def get_config():
    """SECURITY ISSUE: Information disclosure"""
    try:
        # SECURITY ISSUE: Exposing sensitive configuration
        config = {
            "database_url": DB_PATH,
            "database_user": DB_USER,
            "database_password": DB_PASSWORD,
            "stripe_api_key": STRIPE_API_KEY,
            "encryption_key": ENCRYPTION_KEY
        }
        
        return jsonify(config)
    except Exception as e:
        return jsonify({"error": str(e)})

if __name__ == '__main__':
    # SECURITY ISSUE: Debug mode enabled in production
    app.run(debug=True, host='0.0.0.0', port=5000) 