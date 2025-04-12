package traintickets.ui.security;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import traintickets.businesslogic.exception.*;
import traintickets.security.exception.ForbiddenException;
import traintickets.security.exception.UnauthorizedException;
import traintickets.ui.exception.QueryParameterNotFoundException;

public final class ExceptionHandler {
    public static void handle(RuntimeException e, Context ctx) {
        try {
            throw e;
        } catch (QueryParameterNotFoundException ex) {
            ctx.json(e.getMessage());
            ctx.status(HttpStatus.BAD_REQUEST);
        } catch (ForbiddenException ex) {
            ctx.status(HttpStatus.FORBIDDEN);
        } catch (UnauthorizedException ex) {
            ctx.status(HttpStatus.UNAUTHORIZED);
        } catch (EntityAlreadyExistsException | PlaceAlreadyReservedException | TrainAlreadyReservedException ex) {
            ctx.json(ex.getMessage());
            ctx.status(HttpStatus.CONFLICT);
        } catch (EntityNotFoundException ex) {
            ctx.json(ex.getMessage());
            ctx.status(HttpStatus.NOT_FOUND);
        } catch (InvalidEntityException | InvalidPasswordException | PasswordsMismatchesException ex) {
            ctx.json(ex.getMessage());
            ctx.status(HttpStatus.BAD_REQUEST);
        } catch (PaymentException ex) {
            ctx.json(ex.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (UserWasBannedException ex) {
            ctx.json(ex.getMessage());
            ctx.status(HttpStatus.UNAUTHORIZED);
        } catch (RuntimeException ex) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
