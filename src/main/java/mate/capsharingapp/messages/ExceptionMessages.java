package mate.capsharingapp.messages;

public final class ExceptionMessages {
    public static final String CAR_NOT_FOUND_EXCEPTION = "Can't find car by id = %d";
    public static final String NO_CARS_AVAILABLE_EXCEPTION = "No cars available anymore";
    public static final String CHECKING_PAYMENT_EXCEPTION =
            "Error while checking payment for expiration";
    public static final String UNPAID_PAYMENT_EXCEPTION =
            "You cannot create a new rental while you have unpaid rentals";
    public static final String PAYMENT_NOT_FOUND_BY_SESSION_EXCEPTION =
            "Can't find payment by sessionId = %s";
    public static final String PAYMENT_CREATING_EXCEPTION =
            "An error occurred while creating the payment";
    public static final String PAYMENT_SESSION_EXCEPTION =
            "Stripe error with session: %s";
    public static final String RENTAL_NOT_FOUND_EXCEPTION = "Can't find rental by id: %d";
    public static final String RENTAL_ALREADY_RETURNED_EXCEPTION =
            "Rental with id - %d already returned";
    public static final String RENTAL_ACCESS_EXCEPTION =
            "You don't have permission for this rental";
    public static final String EMAIL_EXIST_EXCEPTION = "Email already registered: %s";
    public static final String NOT_FOUND_USER_EXCEPTION = "Can't find user by id: %d";
    public static final String NOT_FOUND_USER_BY_EMAIL_EXCEPTION = "Can't find user by email: %s";
    public static final String INVALID_PASSWORD_EXCEPTION = "Invalid password";
    public static final String AUTHENTICATION_FAILURE_EXCEPTION = "Authentication failed";
    public static final String USER_DO_NOT_REGISTERED_EXCEPTION = "User not registered";
    public static final String ACCESS_DENIED_EXCEPTION =
            "You are not allowed to view rentals of another user";
    public static final String PASSWORD_VALIDATION_EXCEPTION = "Password validation exception";
    public static final String INVALID_COMMAND_FORMAT_MSG =
            "Invalid command format. Use /auth [email] [password]";
}
