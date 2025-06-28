# Production Security Test Repository

This repository is used for testing the X-Pull-Request-Reviewer agent with real production-grade security issues.

## Structure
- `terraform/` - Infrastructure as Code with security issues
- `java/` - Java application code with security vulnerabilities
- `python/` - Python application code with security issues

## Test Scenarios
1. Hardcoded credentials
2. SQL injection vulnerabilities
3. Public access to resources
4. Weak encryption
5. Missing input validation
6. Command injection
7. Path traversal
8. Insecure deserialization

## Usage
This repository is used to test the PR review agent's ability to detect security issues and provide actionable feedback. 