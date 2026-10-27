/* ===========================================================
   V05 · Consulta de asistencia
   API: GET /api/asistencias?idSeccion=&desde=&hasta=
        GET /api/reportes/asistencia  (Excel generado con Apache POI)
   =========================================================== */

let registros = [];

async function iniciar() {
  const usuario = await iniciarPagina('consulta');
  await cargarSecciones('seccion');

  // Por defecto: los ultimos 7 dias
  const hoy = new Date();
  const haceUnaSemana = new Date();
  haceUnaSemana.setDate(hoy.getDate() - 7);
  document.getElementById('desde').value = fechaLocalIso(haceUnaSemana);
  document.getElementById('hasta').value = fechaLocalIso(hoy);

  // Reporte Excel: docente y direccion
  if (['DOCENTE', 'DIRECCION'].includes(usuario.rol)) {
    const boton = document.getElementById('boton-excel');
    boton.hidden = false;
    boton.addEventListener('click', descargarExcel);
  }

  document.getElementById('boton-buscar').addEventListener('click', buscar);
  document.getElementById('estado').addEventListener('change', mostrarTabla);
  document.getElementById('buscar').addEventListener('input', mostrarTabla);

  await buscar();
}

function filtrosUrl() {
  const seccion = document.getElementById('seccion').value;
  const desde = document.getElementById('desde').value;
  const hasta = document.getElementById('hasta').value;
  return `desde=${desde}&hasta=${hasta}` + (seccion ? `&idSeccion=${seccion}` : '');
}

async function buscar() {
  const cajaError = document.getElementById('error');
  cajaError.hidden = true;

  const desde = document.getElementById('desde').value;
  const hasta = document.getElementById('hasta').value;
  if (desde && hasta && desde > hasta) {
    cajaError.textContent = 'La fecha inicial no puede ser posterior a la final';
    cajaError.hidden = false;
    return;
  }

  try {
    registros = await api('/asistencias?' + filtrosUrl());
    const select = document.getElementById('seccion');
    const seccion = select.value ? select.options[select.selectedIndex].text : 'todas las secciones';
    document.getElementById('subtitulo').textContent =
      `Del ${fmtFecha(desde)} al ${fmtFecha(hasta)} · ${seccion}`;
    mostrarTabla();
  } catch (error) {
    cajaError.textContent = error.message;
    cajaError.hidden = false;
  }
}

/* Filtros de estado y nombre aplicados en el navegador */
function mostrarTabla() {
  const estado = document.getElementById('estado').value;
  const texto = document.getElementById('buscar').value.trim().toLowerCase();
  const lista = registros.filter(r =>
    (!estado || r.estado === estado) &&
    (!texto || (r.estudiante + ' ' + r.codigo).toLowerCase().includes(texto)));

  document.getElementById('total').textContent = lista.length;
  document.getElementById('puntuales').textContent = lista.filter(r => r.estado === 'PUNTUAL').length;
  document.getElementById('tardanzas').textContent = lista.filter(r => r.estado === 'TARDANZA').length;
  document.getElementById('faltas').textContent = lista.filter(r => r.estado === 'INASISTENCIA').length;

  document.getElementById('filas').innerHTML = lista.length
    ? lista.map(r => `
        <tr>
          <td>${fmtFecha(r.fecha)}</td>
          <td>${esc(r.estudiante)}<br><small class="canal">${esc(r.codigo)}</small></td>
          <td class="nw">${esc(r.seccion)}</td>
          <td>${r.estado === 'INASISTENCIA' && r.metodo === 'AUTOMATICO' ? '—' : esc(r.hora)}</td>
          <td>${chipEstado(r.estado)}</td>
          <td>${esc(r.metodo)}</td>
          <td class="canal">${r.origen === 'SIN_CONEXION' ? 'Sin conexión' : 'En línea'}</td>
        </tr>`).join('')
    : filaVacia(7);
}

/* Descarga del reporte Excel */
async function descargarExcel() {
  const boton = document.getElementById('boton-excel');
  boton.disabled = true;
  boton.textContent = 'Generando...';
  try {
    const respuesta = await fetch('/api/reportes/asistencia?' + filtrosUrl());
    if (!respuesta.ok) {
      const error = await respuesta.json().catch(() => ({ mensaje: 'No se pudo generar el reporte' }));
      throw new Error(error.mensaje);
    }
    const archivo = await respuesta.blob();
    const enlace = document.createElement('a');
    enlace.href = URL.createObjectURL(archivo);
    enlace.download = `asistencia_${document.getElementById('desde').value}_${document.getElementById('hasta').value}.xlsx`;
    enlace.click();
    setTimeout(() => URL.revokeObjectURL(enlace.href), 1000);
    avisar('Reporte descargado', 'ok');
  } catch (error) {
    avisar(error.message, 'error');
  } finally {
    boton.disabled = false;
    boton.textContent = 'Descargar Excel';
  }
}

iniciar();
