# GKE Cluster with issues
resource "google_container_cluster" "bad_gke" {
  name     = "bad-gke-cluster"
  location = "us-central1"

  # ISSUE: Enable legacy ABAC (should be false)
  enable_legacy_abac = true # SECURITY ISSUE: Legacy ABAC is insecure, use RBAC only

  # ISSUE: No network policy
  remove_default_node_pool = true
  initial_node_count = 1
  # SECURITY ISSUE: No network_policy block, network policies should be enabled

  # ISSUE: Master authorized networks not set
  # SECURITY ISSUE: master_authorized_networks_config block missing, should restrict API access

  # ISSUE: No shielded nodes
  # SECURITY ISSUE: shielded_nodes block missing, should enable shielded nodes
}

# Spanner Instance with issues
resource "google_spanner_instance" "bad_spanner" {
  name         = "bad-spanner-instance"
  config       = "regional-us-central1"
  display_name = "Bad Spanner"
  num_nodes    = 1

  # ISSUE: No labels
  # SECURITY ISSUE: labels block missing, should add labels for ownership and environment
}

# Cloud SQL Instance with issues
resource "google_sql_database_instance" "bad_sql" {
  name             = "bad-sql-instance"
  database_version = "MYSQL_5_6" # SECURITY ISSUE: Outdated version, use latest supported
  region           = "us-central1"

  settings {
    tier = "db-f1-micro" # SECURITY ISSUE: Use production-appropriate tier
    backup_configuration {
      enabled = false # SECURITY ISSUE: Backups should be enabled
    }
    ip_configuration {
      ipv4_enabled    = true # SECURITY ISSUE: Public IP enabled, should use private IP
      authorized_networks = [] # SECURITY ISSUE: Should restrict authorized networks
    }
    # ISSUE: No maintenance window
    # SECURITY ISSUE: maintenance_window block missing, should set maintenance window
  }

  # ISSUE: No deletion protection
  # SECURITY ISSUE: deletion_protection block missing, should enable deletion protection
} 