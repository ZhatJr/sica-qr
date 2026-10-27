/* ===========================================================
   V02 · Panel principal
   Indicadores del dia y accesos directos segun el rol.
   =========================================================== */

const campoFecha = document.getElementById('fecha');

/* Accesos directos por rol */
const ATAJOS = [
  { pagina: 'registro', titulo: 'Registro de ingreso', texto: 'Escanear credenciales QR o registrar por DNI', roles: ['AUXILIAR'] },
  { pagina: 'consulta', titulo: 'Consulta de asistencia', texto: 'Buscar por sección y fechas, y descargar en Excel', roles: ['AUXILIAR', 'DOCENTE', 'DIRECCION'] },
  { pagina: 'credenciales', titulo: 'Credenciales QR', texto: 'Emitir, revocar e imprimir credenciales', roles: ['ADMINISTRADOR'],
    contar: async () => {
      const estudiantes = await api('/estudiantes');
      const sinCredencial = estudiantes.filter(e => !e.token).length;
      return sinCredencial ? `${sinCredencial} estudiantes sin credencial activa` : 'Todos tienen credencial activa';
    } },
  { pagina: 'justificaciones', titulo: 'Justificaciones', texto: '', roles: ['DOCENTE', 'DIRECCION'],
    contar: async () => {
      const lista = await api('/justificaciones');
      return `${lista.filter(j => j.estado === 'PENDIENTE').length} solicitudes pendientes de revisión`;
    } },
  { pagina: 'alertas', titulo: 'Alertas', texto: '', roles: ['DOCENTE', 'DIRECCION'],
    contar: async () => {
      const lista = await api('/alertas');
      return `${lista.filter(a => a.estado !== 'ATENDIDA').length} estudiantes superaron el umbral`;
    } },
  { pagina: 'notificaciones', titulo: 'Notificaciones', texto: '', roles: ['ADMINISTRADOR', 'AUXILIAR'],
    contar: async () => {
      const lista = await api('/notificaciones');
      return `${lista.filter(n => n.estado === 'FALLIDA').length} avisos fallidos por atender`;
    } }
];

async function iniciar() {
  const usuario = await iniciarPagina('panel');

  campoFecha.value = hoyIso();
  campoFecha.max = hoyIso();
  campoFecha.addEventListener('change', cargarIndicadores);
  document.getElementById('boton-hoy').addEventListener('click', () => {
    campoFecha.value = hoyIso();
    cargarIndicadores();
  });

  mostrarAtajos(usuario.rol);
  await cargarIndicadores();
}

async function cargarIndicadores() {
  const fecha = campoFecha.value || hoyIso();
  try {
    const r = await api('/panel/resumen?fecha=' + fecha);
    document.getElementById('puntuales').textContent = r.puntuales;
    document.getElementById('tardanzas').textContent = r.tardanzas;
    document.getElementById('faltas').textContent = r.inasistencias;
    document.getElementById('sin-registro').textContent = r.sinRegistro;

    const asistieron = r.puntuales + r.tardanzas;
    const porcentaje = r.matriculados ? (asistieron * 100 / r.matriculados).toFixed(1) : '0.0';
    document.getElementById('subtitulo').textContent =
      `${fmtFechaLarga(fecha)} · ${r.matriculados} estudiantes matriculados · ${porcentaje.replace('.', ',')} % de asistencia`;

    // Grafico por seccion (roles con acceso a la consulta)
    if (usuarioActual.rol !== 'ADMINISTRADOR') {
      await cargarGraficoSecciones(fecha);
    }
  } catch (error) {
    avisar(error.message, 'error');
  }
}

async function cargarGraficoSecciones(fecha) {
  const registros = await api(`/asistencias?desde=${fecha}&hasta=${fecha}`);
  const panel = document.getElementById('panel-secciones');
  panel.hidden = false;

  // Agrupa los registros del dia por seccion
  const porSeccion = {};
  registros.forEach(r => {
    if (!porSeccion[r.seccion]) porSeccion[r.seccion] = { total: 0, asistieron: 0 };
    porSeccion[r.seccion].total++;
    if (r.estado !== 'INASISTENCIA') porSeccion[r.seccion].asistieron++;
  });

  const secciones = Object.keys(porSeccion).sort();
  document.getElementById('barras').innerHTML = secciones.length
    ? secciones.map(s => {
        const v = Math.round(porSeccion[s].asistieron * 100 / porSeccion[s].total);
        return `<li><span class="etiqueta">${esc(s)}</span>
                    <span class="barra" style="--v:${v}%"></span>
                    <span class="valor">${v} %</span></li>`;
      }).join('')
    : '<li class="vacio" style="display:block">Todavía no hay registros este día.</li>';
}

function mostrarAtajos(rol) {
  const visibles = ATAJOS.filter(a => a.roles.includes(rol));
  document.getElementById('atajos').innerHTML = visibles.map(a => `
    <a class="atajo" href="${a.pagina}.html">
      <div class="ico"><span></span></div>
      <strong>${a.titulo}</strong>
      <small id="atajo-${a.pagina}">${a.texto || 'Cargando...'}</small>
    </a>`).join('');

  visibles.filter(a => a.contar).forEach(async a => {
    try {
      document.getElementById('atajo-' + a.pagina).textContent = await a.contar();
    } catch (e) {
      document.getElementById('atajo-' + a.pagina).textContent = '';
    }
  });
}

iniciar();
