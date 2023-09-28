package com.khan.tickets.demo.data;

import java.time.Instant;

public record UserSummary(Instant joinedAt,
                          Instant lastUpdated,
                          long totalTickets,
                          long totalAssignments,
                          long totalComments) {}
