package traintickets.ui.security;

import io.javalin.http.Context;
import io.javalin.http.ExceptionHandler;
import io.javalin.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import traintickets.businesslogic.exception.*;
import traintickets.businesslogic.logger.UniLogger;
import traintickets.businesslogic.logger.UniLoggerFactory;
import traintickets.security.exception.ForbiddenException;
import traintickets.security.exception.UnauthorizedException;
import traintickets.ui.exception.QueryParameterNotFoundException;
import traintickets.ui.javalin.JsonMapperException;

import java.util.Objects;

public final class RuntimeExceptionHandler implements ExceptionHandler<RuntimeException> {
    private final UniLogger logger;

    public RuntimeExceptionHandler(UniLoggerFactory loggerFactory) {
        logger = Objects.requireNonNull(loggerFactory).getLogger(RuntimeExceptionHandler.class);
    }

    @Override
    public void handle(@NotNull RuntimeException e, @NotNull Context ctx) {
        try {
            throw e;
        } catch (QueryParameterNotFoundException | InvalidEntityException | InvalidPasswordException |
                 PasswordsMismatchesException ex) {
            logger.warn("exception %s '%s'", ex.getClass(), ex.getMessage());
            ctx.json(ex.getMessage() == null ? "" : ex.getMessage());
            ctx.status(HttpStatus.BAD_REQUEST);
        } catch (ForbiddenException ex) {
            logger.warn("exception %s '%s'", ex.getClass(), ex.getMessage());
            ctx.json(ex.getMessage() == null ? "" : ex.getMessage());
            ctx.status(HttpStatus.FORBIDDEN);
        } catch (UnauthorizedException | UserWasBannedException ex) {
            logger.warn("exception %s '%s'", ex.getClass(), ex.getMessage());
            ctx.json(ex.getMessage() == null ? "" : ex.getMessage());
            ctx.status(HttpStatus.UNAUTHORIZED);
        } catch (EntityAlreadyExistsException | PlaceAlreadyReservedException | TrainAlreadyReservedException ex) {
            logger.warn("exception %s '%s'", ex.getClass(), ex.getMessage());
            ctx.json(ex.getMessage() == null ? "" : ex.getMessage());
            ctx.status(HttpStatus.CONFLICT);
        } catch (EntityNotFoundException ex) {
            logger.warn("exception %s '%s'", ex.getClass(), ex.getMessage());
            ctx.json(ex.getMessage() == null ? "" : ex.getMessage());
            ctx.status(HttpStatus.NOT_FOUND);
        } catch (PaymentException ex) {
            logger.error("exception %s '%s' on %s", ex.getClass(), ex.getMessage(), ex.getStackTrace());
            ctx.json(ex.getMessage() == null ? "" : ex.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (JsonMapperException ex) {
            logger.error("exception %s '%s' on %s", ex.getClass(), ex.getMessage(), ex.getStackTrace());
            ctx.json(ex.getMessage() == null ? "" : ex.getMessage());
            ctx.status(HttpStatus.BAD_REQUEST);
        } catch (RuntimeException ex) {
            logger.error("exception %s '%s' on %s", ex.getClass(), ex.getMessage(), ex.getStackTrace());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
