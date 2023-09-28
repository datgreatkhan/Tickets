export function testEmailMapping(role) {
  switch(role) {
    case "USER":
      return "test.user@tickets.com";
    case "SUPPORT":
      return "test.support@tickets.com";
    case "ADMIN":
      return "test.admin@tickets.com";
    default:
      return "tickets.null@test.com";
  }
}

export function getRoleColor(role) {
  switch(role) {
    case "USER":
      return "green";
    case "SUPPORT":
      return "indigo";
    case "ADMIN":
      return "red";
    default:
      return "default";
  }
}

export function getTypeColor(type) {
  switch(type) {
    case "ISSUE":
      return "red";
    case "INQUIRY":
      return "blue";
    case "REPORT":
      return "gray";
    case "OTHER":
      return "default";
    default:
      return "default";
  }
}

export function getStatusColor(status) {
  switch(status) {
    case "UNASSIGNED":
      return "black";
    case "OPEN":
      return "green";
    case "CLOSED":
      return "red";
    default:
      return "white";
  }
}
