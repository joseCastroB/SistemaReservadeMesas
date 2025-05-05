<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Lista de Estudiantes</title>
</head>
<body>

<c:if test="${empty productos}">
    <p>No hay estudiantes registrados.</p>
</c:if>

<c:forEach items="${estudiantes}" var="estudiante" varStatus="status">
    <div>
        <h3>${status.count}. ${estudiante.nombre}</h3>
        <p>Apellido: <c:out value="${estudiante.apellido}" /> </p>

        <c:set var="resultado" value="${fn:containsIgnoreCase(estudiante.nombre, 'Roberto')}"/>
        <c:if test="${resultado}">
            <p>Nombre coincide con 'Roberto'</p>
        </c:if>

    </div>
</c:forEach>

<c:url value="/productos" var="urlPaginacion">
    <c:param name="pagina" value="${paginaActual + 1}" />
</c:url>
<a href="${urlPaginacion}">Siguiente página</a>



</body>
</html>