import { apiClient } from '../plugins/index.js';

export function getTickets(page, sortBy) {
  return apiClient.get('ticket/user/all/' + page + '/' + sortBy.ref + '/' + sortBy.desc);
}

export function getTicket(id) {
  return apiClient.get('ticket/user/' + id);
}

export function createTicket(ticket) {
  return apiClient.post('ticket/new',
    ticket);
}

export function editTicket(ticket, changeTo) {
  return apiClient.post('ticket/edit', {
    ticket: ticket,
    changeTo: changeTo
  })
}

export function replyToTicket(comment) {
  return apiClient.post('ticket/reply',
    comment);
}

// Support or Admin Role

export function getAllTickets(page, sortBy) {
  return apiClient.get('ticket/all/' + page + '/' + sortBy.ref + '/' + sortBy.desc);
}

export function getAssignedTickets(page, sortBy) {
  return apiClient.get('ticket/user/assigned/' + page + '/' + sortBy.ref + '/' + sortBy.desc);
}

export function getUsersTickets(id, page, sortBy) {
  return apiClient.get('ticket/all/user/' + id + '/' + page + '/' + sortBy.ref + '/' + sortBy.desc);
}

export function getSpecificTicket(id) {
  return apiClient.get('ticket/' + id);
}

export function updateAssignees(id, assignees) {
  return apiClient.post('ticket/update-assignees', {
    ticket: id,
    assignees: assignees
  })
}

export function updateStatus(id, status) {
  return apiClient.post('ticket/update-status', {
    ticket: id,
    status: status
  })
}

export function assignTicket(assignment) {
  return apiClient.post('ticket/assign',
    assignment);
}

export function unassignTicket() {}
