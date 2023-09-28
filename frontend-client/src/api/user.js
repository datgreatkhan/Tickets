import { apiClient } from '../plugins/index.js';

export function getUserSummary() {
  return apiClient.get('user/summary');
}

// Admin or Support

export function getUser(id) {
  return apiClient.get('user/' + id);
}

export function getUsersSummary(id) {
  return apiClient.get('user/summary/' + id);
}

export function getAllUsers(page, sortBy) {
  return apiClient.get('user/all/' + page + '/' + sortBy.ref + '/' + sortBy.desc);
}

export function getAllAdminOrSupportUsers(page) {
  return apiClient.get('user/all/admin-or-support/' + page);
}
