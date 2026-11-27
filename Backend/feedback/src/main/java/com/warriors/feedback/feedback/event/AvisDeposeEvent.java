package com.warriors.feedback.feedback.event;

import java.time.LocalDateTime;

public record AvisDeposeEvent(
        Long avisId,
        Long evenementId,
        Long participantId,
        int note,
        LocalDateTime date
) {}
