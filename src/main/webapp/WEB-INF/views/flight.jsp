<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html>
<head>
    <title>View Flights</title>
</head>
<body>

<h2>Upcoming Journeys</h2>

<table border="1" cellpadding="10">
    <tr>
        <th>Flight</th>
        <th>Source</th>
        <th>Destination</th>
        <th>Available Seats</th>
        <th>Price</th>
    </tr>
    <c:forEach var="flight" items="${upcoming}">
        <tr>
            <td>${flight.flightNumber}</td>
            <td>${flight.source}</td>
            <td>${flight.destination}</td>
            <td>${flight.availableSeats}</td>
            <td>&#8377;${flight.pricePerSeat}</td>
        </tr>
    </c:forEach>
</table>

<h2>Completed Journeys</h2>

<table border="1" cellpadding="10">
    <tr>
        <th>Flight</th>
        <th>Source</th>
        <th>Destination</th>
    </tr>
    <c:forEach var="flight" items="${completed}">
        <tr>
            <td>${flight.flightNumber}</td>
            <td>${flight.source}</td>
            <td>${flight.destination}</td>
        </tr>
    </c:forEach>
</table>

</body>
</html>