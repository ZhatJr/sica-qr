/* ===========================================================
   V06 · Seguimiento de notificaciones (ADMINISTRADOR y AUXILIAR)
   API: GET /api/notificaciones
   =========================================================== */

let notificaciones = [];
const NOMBRE_CANAL = { WHATSAPP: 'WhatsApp', CORREO: 'Correo', SMS: 'SMS' };

async function iniciar() {
  await iniciarPagina('notificaciones');
  ['estado', 'canal'].forEach(id => document.getElementById(id).addEventListener('change', mostrarTabla));
  document.getElementById('buscar').addEventListener('input', mostrarTabla);

  try {
    notificaciones = await api('/notificaciones');
    document.getElementById('enviadas').textContent = contar('ENVIADA');
    document.getElementById('pendientes').textContent = contar('PENDIENTE');
    document.getElementById('fallidas').textContent = contar('FALLIDA');
    document.getElementById('total').textContent = notificaciones.length;
    mostrarTabla();
  } catch (error) {
    avisar(error.message, 'error');
  }
}

function contar(estado) {
  return notificaciones.filter(n => n.estado === estado).length;
}

function mostrarTabla() {
  const estado = document.getElementById('estado').value;
  const canal = document.getElementById('canal').value;
  const texto = document.getElementById('buscar').value.trim().toLowerCase();

  const lista = notificaciones.filter(n =>
    (!estado || n.estado === estado) &&
    (!canal || n.canal === canal) &&
    (!texto || (n.estudiante + ' ' + n.apoderado).toLowerCase().includes(texto)));

  document.getElementById('filas').innerHTML = lista.length
    ? lista.map(n => `
        <tr>
          <td>${fmtFechaHora(n.creadaEn)}</td>
          <td>${esc(n.estudiante)}</td>
          <td>${esc(n.apoderado)}</td>
          <td class="canal">${NOMBRE_CANAL[n.canal] || esc(n.canal)}</td>
          <td class="canal">${esc(n.destino)}</td>
          <td>${n.intentos}</td>
          <td>${chipEstado(n.estado)}${n.detalleError ? `<br><small class="canal">${esc(n.detalleError)}</small>` : ''}</td>
        </tr>`).join('')
    : filaVacia(7);
}

iniciar();
