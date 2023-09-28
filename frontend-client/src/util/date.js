const DATE_TIME = new Intl.DateTimeFormat(undefined, {
  dateStyle: "short",
  timeStyle: "medium"
})

const DATE = new Intl.DateTimeFormat(undefined, {
  dateStyle: "short",
})

export { DATE_TIME, DATE }

export function toDateTime(value) {
  return DATE_TIME.format(Date.parse(value)).replace(',', ' @');
}

export function toDate(value) {
  return DATE.format(Date.parse(value));
}
