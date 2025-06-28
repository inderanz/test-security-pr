# Good Terraform configuration
terraform {
  required_version = ">= 1.0"
  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "~> 4.0"
    }
  }
}

# Variables for secure configuration
variable "project_id" {
  description = "GCP Project ID"
  type        = string
}

variable "region" {
  description = "GCP Region"
  type        = string
  default     = "us-central1"
}

# Secure VPC configuration
resource "google_compute_network" "vpc" {
  name                    = "secure-vpc"
  auto_create_subnetworks = false
  description             = "Secure VPC for production"
}

# Secure subnet
resource "google_compute_subnetwork" "subnet" {
  name          = "secure-subnet"
  ip_cidr_range = "10.0.1.0/24"
  network       = google_compute_network.vpc.name
  region        = var.region
  
  private_ip_google_access = true
}

# Secure firewall rule
resource "google_compute_firewall" "allow_ssh" {
  name    = "allow-ssh"
  network = google_compute_network.vpc.name
  
  allow {
    protocol = "tcp"
    ports    = ["22"]
  }
  
  source_ranges = ["10.0.1.0/24"]
  target_tags   = ["ssh"]
} 