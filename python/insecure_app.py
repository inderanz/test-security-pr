# SECURITY ISSUE: Exposed admin endpoint
from flask import Flask, request
app = Flask(__name__)

@app.route('/admin', methods=['GET'])
def admin():
    return 'Admin panel: no authentication!'

# ... existing code ... 