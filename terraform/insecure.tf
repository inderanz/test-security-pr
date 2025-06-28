# SECURITY ISSUES: Insecure Terraform configuration

# SECURITY ISSUE: Hardcoded credentials
variable "db_password" {
  default = "super_secret_password123"  # Should use environment variables
}

# SECURITY ISSUE: Public access to storage bucket
resource "google_storage_bucket" "public_data" {
  name          = "prod-public-data-bucket"
  location      = "US"
  force_destroy = true
  
  # SECURITY ISSUE: Public read access
  uniform_bucket_level_access = false
}

# SECURITY ISSUE: Public IAM policy
resource "google_storage_bucket_iam_member" "public_read" {
  bucket = google_storage_bucket.public_data.name
  role   = "roles/storage.objectViewer"
  member = "allUsers"  # Should never use allUsers
}

# SECURITY ISSUE: Overly permissive firewall rule
resource "google_compute_firewall" "allow_all" {
  name    = "allow-all-traffic"
  network = "default"
  
  # SECURITY ISSUE: Allow all traffic
  allow {
    protocol = "tcp"
    ports    = ["0-65535"]  # Should specify exact ports
  }
  
  # SECURITY ISSUE: Allow all sources
  source_ranges = ["0.0.0.0/0"]  # Should be specific CIDR blocks
}

# SECURITY ISSUE: Weak encryption key
resource "google_kms_crypto_key" "weak_key" {
  name     = "prod-encryption-key"
  key_ring = "projects/my-project/locations/us-central1/keyRings/my-keyring"
  
  # SECURITY ISSUE: No rotation period
  # SECURITY ISSUE: No protection level specified
  
  lifecycle {
    prevent_destroy = false  # Should be true for production
  }
}

# SECURITY ISSUE: Public Cloud Run service
resource "google_cloud_run_service" "public_api" {
  name     = "prod-public-api"
  location = "us-central1"
  
  template {
    spec {
      containers {
        image = "gcr.io/my-project/api:latest"
        
        # SECURITY ISSUE: Hardcoded database URL
        env {
          name  = "DATABASE_URL"
          value = "mysql://admin:password123@localhost:3306/prod_db"
        }
      }
    }
  }
  
  # SECURITY ISSUE: Allow unauthenticated invocations
  traffic {
    percent         = 100
    latest_revision = true
  }
}

# SECURITY ISSUE: Public IAM policy for Cloud Run
resource "google_cloud_run_service_iam_member" "public_access" {
  location = google_cloud_run_service.public_api.location
  service  = google_cloud_run_service.public_api.name
  role     = "roles/run.invoker"
  member   = "allUsers"  # Should never use allUsers
} 