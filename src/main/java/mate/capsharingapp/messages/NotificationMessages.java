package mate.capsharingapp.messages;

public final class NotificationMessages {
    public static final String PAYMENT_SUCCESS_NOTIFICATION =
            """
                    Payment Successful

                    Dear Admins,

                    We are pleased to inform you that a payment has been successfully processed.\s
                    Below are the details of the rental, car, and the associated user:

                    Rental Details:
                    - Rental ID: %s
                    - Car Model: %s
                    - Rental Date: %s
                    - Scheduled Return Date: %s
                    - Payment Amount: %s

                    User Details:
                    - User ID: %s
                    - User Name: %s %s
                    - Email: %s

                    Please confirm the payment and update the rental status if necessary.

                    Thank you!""";
    public static final String PAYMENT_CANCELED_MESSAGE =
            "Payment session canceled. You can complete the payment within 24 hours";
    public static final String SUCCESS_COMPLETE_MESSAGE = "Payment successful. Thank you!";
    public static final String SUCCESS_NOT_COMPLETE_MESSAGE = "Payment not completed";
    public static final String OVERDUE_RENTAL_NOTIFICATION =
            """
                    Overdue Rental Alert

                    Dear Admins,

                    We would like to inform you about overdue rentals.
                    Below are the details of the rentals and the associated users:

                    %s

                    Please take the necessary actions to address these overdue rentals.

                    Thank you.
                    """;

    public static final String RENTAL_DETAILS_TEMPLATE =
            """
                    Rental Details:
                    - Rental ID: %s
                    - Car Model: %s
                    - Rental Date: %s
                    - Scheduled Return Date: %s
                    User Details:
                    - User ID: %s
                    - User Name: %s %s
                    - Email: %s
                    """;
    public static final String RENTAL_CREATE_NOTIFICATION =
            """
                    Successful Car Rental

                    Dear Admins,

                    We are pleased to inform you about a successful car rental.\s
                    Below are the details of the rental and the associated user:

                    Rental Details:
                    - Rental ID: %s
                    - Car Model: %s
                    - Rental Date: %s
                    - Scheduled Return Date: %s

                    User Details:
                    - User ID: %s
                    - User Name: %s %s
                    - Email: %s

                    Please ensure everything is in order for this rental.

                    Thank you.""";
}