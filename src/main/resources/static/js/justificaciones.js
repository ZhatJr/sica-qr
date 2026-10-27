/* ===========================================================
   V07 · Gestion de justificaciones (DOCENTE y DIRECCION)
   API: GET /api/justificaciones
   =========================================================== */

let justificaciones = [];
let seleccionada = null;

async function iniciar() {
  await iniciarPagina('justificaciones');
  document.getElementById('estado').addEventListener('change', mostrarLista);

  try {
    justificaciones = await api('/justificaciones');
    const pendientes = justificaciones.filter(j => j.estado === 'PENDIENTE').length;
    document.getElementById('subtitulo').textContent =
      `${pendientes} ${pendientes === 1 ? 'solicitud pendiente' : 'solicitudes pendientes'} de revisión`;
    mostrarLista();
  } catch (error) {
    avisar(error.message, 'error');
  }
}

function mostrarLista() {
  const estado = document.getElementById('estado').value;
  const lista = justificaciones.filter(j => !estado || j.estado === estado);
  const contenedor = document.getElementById('lista');

  if (!lista.length) {
    contenedor.innerHTML = '<p class="vacio">No hay solicitudes.</p>';
    return;
  }

  contenedor.innerHTML = lista.map(j => `
    <article class="solicitud ${j.idJustificacion === seleccionada ? 'seleccionada' : ''}" data-id="${j.idJustificacion}">
      <div>
        <strong>${esc(j.estudiante)}</strong>
        <span>${esc(j.seccion)} · ${TEXTO_ESTADO[j.estadoAsistencia] || ''} del ${fmtFecha(j.fechaFalta)}</span>
      </div>
      ${chipEstado(j.estado)}
    </article>`).join('');

  contenedor.querySelectorAll('.solicitud').forEach(item =>
    item.addEventListener('click', () => {
      seleccionada = Number(item.dataset.id);
      mostrarLista();
      mostrarDetalle();
    }));
}

function mostrarDetalle() {
  const j = justificaciones.find(x => x.idJustificacion === seleccionada);
  document.getElementById('detalle').innerHTML = `
    <h2>${esc(j.estudiante)}</h2>
    <dl>
      <dt>Sección</dt><dd>${esc(j.seccion)}</dd>
      <dt>Fecha justificada</dt><dd>${fmtFecha(j.fechaFalta)} · ${TEXTO_ESTADO[j.estadoAsistencia] || ''}</dd>
      <dt>Motivo</dt><dd>${esc(j.motivo)}</dd>
      <dt>Presentada</dt><dd>${fmtFechaHora(j.presentadaEn)}</dd>
      <dt>Estado</dt><dd>${chipEstado(j.estado)}</dd>
      <dt>Revisada por</dt><dd>${j.revisor ? esc(j.revisor) : '—'}</dd>
    </dl>
    <p class="advertencia">
      Al aprobarla, el registro seguirá figurando con su estado original, pero dejará de
      contar para el umbral de alertas.
    </p>
    <div class="acciones">
      <button class="boton-principal" disabled title="Disponible en la siguiente versión">Aprobar justificación</button>
      <button class="boton-peligro" disabled title="Disponible en la siguiente versión">Rechazar</button>
    </div>`;
}

iniciar();
