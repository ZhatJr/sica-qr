/* ===========================================================
   SICA-QR · Funciones comunes de las pantallas
   Llamadas a la API, sesion y menu por rol, escape de HTML y formatos.
   =========================================================== */

/* -----------------------------------------------------------
   Menu del sistema y roles que pueden ver cada pantalla
   (TABLA 25 del informe: mapeo de ventanas y usuarios)
   ----------------------------------------------------------- */
const MENU = [
  { pagina: 'panel',           texto: 'Panel principal',        roles: ['ADMINISTRADOR', 'AUXILIAR', 'DOCENTE', 'DIRECCION'] },
  { pagina: 'registro',        texto: 'Registro de ingreso',    roles: ['AUXILIAR'] },
  { pagina: 'consulta',        texto: 'Consulta de asistencia', roles: ['AUXILIAR', 'DOCENTE', 'DIRECCION'] },
  { pagina: 'credenciales',    texto: 'Credenciales QR',        roles: ['ADMINISTRADOR'] },
  { pagina: 'justificaciones', texto: 'Justificaciones',        roles: ['DOCENTE', 'DIRECCION'] },
  { pagina: 'alertas',         texto: 'Alertas',                roles: ['DOCENTE', 'DIRECCION'] },
  { pagina: 'notificaciones',  texto: 'Notificaciones',         roles: ['ADMINISTRADOR', 'AUXILIAR'] }
];

const NOMBRE_ROL = {
  ADMINISTRADOR: 'Administrador',
  AUXILIAR: 'Auxiliar de educación',
  DOCENTE: 'Docente / Tutor',
  DIRECCION: 'Dirección'
};

let usuarioActual = null;

/* -----------------------------------------------------------
   Llamada a la API REST
   ----------------------------------------------------------- */
async function api(ruta, metodo = 'GET', datos = null) {
  const opciones = { method: metodo, headers: {} };
  if (datos !== null) {
    opciones.headers['Content-Type'] = 'application/json';
    opciones.body = JSON.stringify(datos);
  }

  const respuesta = await fetch('/api' + ruta, opciones);

  // Sin sesion o sesion vencida: volver al inicio de sesion
  if (respuesta.status === 401) {
    window.location.href = 'index.html';
    throw new Error('Sesión vencida');
  }

  if (!respuesta.ok) {
    let mensaje = 'Error ' + respuesta.status;
    try {
      mensaje = (await respuesta.json()).mensaje;
    } catch (e) { /* la respuesta no era JSON */ }
    throw new Error(mensaje);
  }

  if (respuesta.status === 204) return null;
  return respuesta.json();
}

/* -----------------------------------------------------------
   Inicio de cada pantalla: verifica sesion, rol y arma el menu
   ----------------------------------------------------------- */
async function iniciarPagina(paginaActual) {
  usuarioActual = await api('/auth/sesion');

  const opcion = MENU.find(m => m.pagina === paginaActual);
  if (opcion && !opcion.roles.includes(usuarioActual.rol)) {
    // Pantalla no autorizada para el rol
    window.location.href = 'panel.html';
    throw new Error('Sin permiso');
  }

  armarMenu(paginaActual);
  vigilarConexion();
  return usuarioActual;
}

function armarMenu(paginaActual) {
  const enlaces = MENU
    .filter(m => m.roles.includes(usuarioActual.rol))
    .map(m => `<a href="${m.pagina}.html" class="${m.pagina === paginaActual ? 'activo' : ''}">${m.texto}</a>`)
    .join('');

  document.getElementById('menu').innerHTML = `
    <div class="menu-marca">
      <div class="logo-qr chico" aria-hidden="true"><span></span><span></span><span></span><span></span></div>
      <strong>SICA-QR</strong>
    </div>
    <nav>${enlaces}</nav>
    <div class="menu-usuario">
      <span>${esc(usuarioActual.nombreCompleto)}</span>
      <small>${NOMBRE_ROL[usuarioActual.rol] || usuarioActual.rol}</small>
      <br><button type="button" id="boton-salir">Cerrar sesión</button>
    </div>`;

  document.getElementById('boton-salir').addEventListener('click', cerrarSesion);
}

async function cerrarSesion() {
  try { await api('/auth/logout', 'POST'); } catch (e) { /* igual se sale */ }
  window.location.href = 'index.html';
}

/* Pastilla "Conectado / Sin conexion" de la cabecera */
function vigilarConexion() {
  const pastilla = document.getElementById('conexion');
  if (!pastilla) return;
  const actualizar = () => {
    pastilla.textContent = navigator.onLine ? 'Conectado' : 'Sin conexión';
    pastilla.classList.toggle('sin-conexion', !navigator.onLine);
  };
  window.addEventListener('online', actualizar);
  window.addEventListener('offline', actualizar);
  actualizar();
}

/* Escapa caracteres HTML de los datos antes de mostrarlos (prevencion de XSS) */
function esc(texto) {
  if (texto === null || texto === undefined) return '';
  return String(texto)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;');
}

/* -----------------------------------------------------------
   Formatos
   ----------------------------------------------------------- */
const DIAS = ['domingo', 'lunes', 'martes', 'miércoles', 'jueves', 'viernes', 'sábado'];
const MESES = ['enero', 'febrero', 'marzo', 'abril', 'mayo', 'junio', 'julio',
               'agosto', 'setiembre', 'octubre', 'noviembre', 'diciembre'];

/** "2026-10-23" -> "23/10/2026" */
function fmtFecha(iso) {
  if (!iso) return '—';
  const [a, m, d] = iso.substring(0, 10).split('-');
  return `${d}/${m}/${a}`;
}

/** "2026-10-23T07:35:00" -> "23/10/2026 07:35" */
function fmtFechaHora(iso) {
  if (!iso) return '—';
  return fmtFecha(iso) + ' ' + iso.substring(11, 16);
}

/** "2026-10-23" -> "viernes 23 de octubre de 2026" */
function fmtFechaLarga(iso) {
  const [a, m, d] = iso.split('-').map(Number);
  const fecha = new Date(a, m - 1, d);
  return `${DIAS[fecha.getDay()]} ${d} de ${MESES[m - 1]} de ${a}`;
}

/** Fecha de hoy en formato ISO (yyyy-mm-dd), con la hora local */
function hoyIso() {
  return fechaLocalIso(new Date());
}

function fechaLocalIso(fecha) {
  const p = n => String(n).padStart(2, '0');
  return `${fecha.getFullYear()}-${p(fecha.getMonth() + 1)}-${p(fecha.getDate())}`;
}

/** Fecha y hora local sin zona horaria: "2026-10-27T07:35:12" */
function fechaHoraLocalIso(fecha) {
  const p = n => String(n).padStart(2, '0');
  return `${fechaLocalIso(fecha)}T${p(fecha.getHours())}:${p(fecha.getMinutes())}:${p(fecha.getSeconds())}`;
}

/* Etiqueta de color para cada estado */
const CLASE_ESTADO = {
  PUNTUAL: 'puntual', TARDANZA: 'tardanza', INASISTENCIA: 'falta',
  ACTIVA: 'puntual', REVOCADA: 'falta', VENCIDA: 'falta',
  PENDIENTE: 'pendiente-chip', APROBADA: 'aprobado', RECHAZADA: 'falta',
  ENVIADA: 'puntual', FALLIDA: 'falta',
  EN_ATENCION: 'tardanza', ATENDIDA: 'aprobado'
};

const TEXTO_ESTADO = {
  PUNTUAL: 'Puntual', TARDANZA: 'Tardanza', INASISTENCIA: 'Inasistencia',
  ACTIVA: 'Activa', REVOCADA: 'Revocada', VENCIDA: 'Vencida',
  PENDIENTE: 'Pendiente', APROBADA: 'Aprobada', RECHAZADA: 'Rechazada',
  ENVIADA: 'Enviada', FALLIDA: 'Fallida',
  EN_ATENCION: 'En atención', ATENDIDA: 'Atendida'
};

function chipEstado(estado, textoExtra = '') {
  const clase = CLASE_ESTADO[estado] || 'pendiente-chip';
  const texto = (TEXTO_ESTADO[estado] || estado) + (textoExtra ? ' · ' + textoExtra : '');
  return `<span class="estado ${clase}">${esc(texto)}</span>`;
}

/* Llena un <select> de secciones */
async function cargarSecciones(idSelect, textoTodas = 'Todas las secciones') {
  const secciones = await api('/secciones');
  const select = document.getElementById(idSelect);
  select.innerHTML = `<option value="">${textoTodas}</option>` +
    secciones.map(s => `<option value="${s.idSeccion}">${esc(s.etiqueta)}</option>`).join('');
  return secciones;
}

/* Aviso que aparece unos segundos en la esquina */
function avisar(texto, tipo = '') {
  let aviso = document.getElementById('aviso-flotante');
  if (!aviso) {
    aviso = document.createElement('div');
    aviso.id = 'aviso-flotante';
    document.body.appendChild(aviso);
  }
  aviso.className = 'aviso-flotante ' + tipo;
  aviso.textContent = texto;
  aviso.style.display = 'block';
  clearTimeout(aviso.temporizador);
  aviso.temporizador = setTimeout(() => { aviso.style.display = 'none'; }, 4000);
}

/* Fila de "no hay datos" para las tablas */
function filaVacia(columnas, texto = 'No hay registros para los filtros seleccionados.') {
  return `<tr><td colspan="${columnas}" class="vacio">${texto}</td></tr>`;
}
