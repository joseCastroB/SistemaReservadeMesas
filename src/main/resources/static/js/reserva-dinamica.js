document.addEventListener("DOMContentLoaded", function() {
    const fechaInput = document.getElementById('fecha');
    const franjaSelect = document.getElementById('idFranja');
    const tipoMesaSelect = document.getElementById('idTipoMesa');

    function actualizarFranjas() {
        const fechaSeleccionada = fechaInput.value;
        if (!fechaSeleccionada) {
            franjaSelect.innerHTML = '<option value="">Selecciona una fecha primero</option>';
            tipoMesaSelect.innerHTML = '<option value="">Selecciona una franja primero</option>';
            return;
        }
        
        fetch(`/api/disponibilidad?fecha=${fechaSeleccionada}`)
            .then(response => response.json())
            .then(data => {
                franjaSelect.innerHTML = '<option value="">Selecciona un horario</option>';
                data.forEach(franja => {
                    let optionText = franja.estaCasiLleno ? 
                        `${franja.franjaHoraria} - (Ocupado +70%)` : 
                        `${franja.franjaHoraria} - ${franja.mesasDisponibles} mesas disponibles`;
                    
                    const option = new Option(optionText, franja.idFranja);
                    if (franja.mesasDisponibles <= 0 || franja.estaCasiLleno) {
                        option.disabled = true;
                    }
                    franjaSelect.appendChild(option);
                });
            });
    }

    function actualizarTiposDeMesa() {
        const fechaSeleccionada = fechaInput.value;
        const franjaSeleccionada = franjaSelect.value;
        
        if (!fechaSeleccionada || !franjaSeleccionada) {
            tipoMesaSelect.innerHTML = '<option value="">Selecciona una franja primero</option>';
            return;
        }
        
        fetch(`/api/disponibilidad-tipos?fecha=${fechaSeleccionada}&idFranja=${franjaSeleccionada}`)
            .then(response => response.json())
            .then(data => {
                tipoMesaSelect.innerHTML = '<option value="">Selecciona tipo de mesa</option>';
                data.forEach(tipo => {
                    const optionText = `${tipo.nombreTipoMesa} (${tipo.mesasDisponibles} disponibles)`;
                    const option = new Option(optionText, tipo.idTipoMesa);
                    if (tipo.mesasDisponibles <= 0) {
                        option.disabled = true;
                    }
                    tipoMesaSelect.appendChild(option);
                });
            });
    }

    fechaInput.addEventListener('change', function() {
        actualizarFranjas();
        // Limpiamos el select de tipo de mesa al cambiar la fecha
        tipoMesaSelect.innerHTML = '<option value="">Selecciona una franja primero</option>';
    });
    
    franjaSelect.addEventListener('change', actualizarTiposDeMesa);

    // Cargar franjas si ya hay una fecha seleccionada al cargar la página
    if(fechaInput.value) {
        actualizarFranjas();
    }
});
