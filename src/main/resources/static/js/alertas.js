/* ===========================================================
   V08 · Alertas de acumulacion (DOCENTE y DIRECCION)
   API: GET /api/alertas
   =========================================================== */

let alertas = [];
const TEXTO_NIVEL = { CRITICA: 'Crítica', PREVENTIVA: 'Preventiva', INFORMATIVA: 'Informativa' };

async function iniciar() {
  await iniciarPagina('alertas');
  ['nivel', 'estado'].forEach(id => document.getElementById(id).addEventListener('change', mostrarTabla));

  try {
    alertas = await api('/alertas');
    document.getElementById('total').textContent = alertas.length;
    document.getElementById('criticas').textContent = alertas.filter(a => a.nivel === 'CRITICA').length;
    document.getElementById('preventivas').textContent = alertas.filter(a => a.nivel === 'PREVENTIVA').length;
    document.getElementById('pendientes').textContent = alertas.filter(a => a.estado === 'PENDIENTE').length;
    mostrarTabla();
  } catch (error) {
    avisar(error.message, 'error');
  }
}

function mostrarTabla() {
  const nivel = document.getElementById('nivel').value;
  const estado = document.getElementById('estado').value;
  const lista = alertas.filter(a => (!nivel || a.nivel === nivel) && (!estado || a.estado === estado));

  document.getElementById('filas').innerHTML = lista.length
    ? lista.map(a => `
        <tr>
          <td>${esc(a.estudiante)}</td>
          <td>${esc(a.seccion)}</td>
          <td>${a.cantidad} ${a.tipo === 'TARDANZAS' ? 'tardanzas' : 'inasistencias'} acumuladas</td>
          <td><span class="nivel ${esc(a.nivel)}">${TEXTO_NIVEL[a.nivel] || esc(a.nivel)}</span></td>
          <td>${chipEstado(a.estado)}</td>
          <td>${fmtFechaHora(a.generadaEn)}</td>
        </tr>`).join('')
    : filaVacia(6);
}

iniciar();
