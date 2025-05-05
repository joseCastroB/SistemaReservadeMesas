<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Lista de Estudiantes</title>
</head>
<body>
    <c:forEach items="${estudiantes}" var="estudiante" varStatus="status">
        <div>
            <h3>${status.count}. ${estudiante.nombre}</h3>
            <p>Fecha Nac. : <fmt:formatDate value="${estudiante.fecha_nacimiento}" pattern="dd/MM/yyyy" /> </p>
        </div>
    </c:forEach>

    <c:set var="mensaje" value="Hola mundo!" />
    <p>${fn:replace(mensaje, 'mundo', 'JSTL')}</p>


</body>
</html>