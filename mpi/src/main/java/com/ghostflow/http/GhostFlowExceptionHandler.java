package com.ghostflow.http;

import com.ghostflow.http.security.GhostFlowAccessDeniedException;
import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.function.Function;

@RestController
@ControllerAdvice
public class GhostFlowExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log;

    private final static ImmutableMap<Class<? extends Throwable>, Message> typeToMessage;

    static {
        log = LoggerFactory.getLogger(GhostFlowExceptionHandler.class);
        typeToMessage = ImmutableMap.<Class<? extends Throwable>, Message>builder()
            .put(IllegalArgumentException.class, new Message(HttpStatus.BAD_REQUEST))
            .put(GhostFlowAccessDeniedException.class, new Message(HttpStatus.FORBIDDEN))
            .put(DataIntegrityViolationException.class, Message.INTERNAL)
            .put(BadSqlGrammarException.class, Message.INTERNAL)
            .build();
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<?> handleException(Exception e) {
        log.warn("Error", e);
        Message message = typeToMessage.getOrDefault(e.getClass(), Message.DEFAULT);
        return new ResponseEntity<>(ImmutableMap.of(
            "error", message.getMessageFunction().apply(e),
            "status", message.getStatus().getReasonPhrase()
        ), message.getStatus());
    }

    @NoArgsConstructor(force = true)
    @AllArgsConstructor
    @Getter
    private static class Message {
        private static final Message DEFAULT;
        private static final Message INTERNAL;

        static {
            DEFAULT = new Message(HttpStatus.INTERNAL_SERVER_ERROR);
            INTERNAL = new Message(HttpStatus.INTERNAL_SERVER_ERROR, e -> "Internal Server Error");
        }

        private final HttpStatus status;
        private final Function<Exception, String> messageFunction;

        public Message(HttpStatus status) {
            this(status, Throwable::getMessage);
        }
    }
}