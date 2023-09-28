import {toDateTime} from "@/util/date";

export function formatUserSummary(data) {
  const formatted = {
    primary: {
      header: "Join Date",
      metric: toDateTime(data.joinedAt)
    },
    secondary: {
      header: "Last Updated",
      metric: toDateTime(data.lastUpdated)
    }
  }

  return formatted;
}

export function formatTicketSummary(data) {
  const formatted = {
    primary: {
      header: "Tickets Made",
      metric: data.totalTickets
    },
    secondary: {
      header: "Tickets Assigned",
      metric: data.totalAssignments
    },
    tertiary: {
      header: "Comments Made",
      metric: data.totalComments
    }
  }

  return formatted;
}
