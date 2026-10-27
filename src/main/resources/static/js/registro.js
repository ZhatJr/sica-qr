/* ===========================================================
   V04 · Registro de ingreso (AUXILIAR)
   API: POST /api/asistencias/qr · POST /api/asistencias/dni
   Cada lectura se envia con su hora de captura y un identificador unico.
   Sin conexion (RF-03), la lectura se guarda en una cola local y se
   reenvia automaticamente al recuperar la conexion.
   =========================================================== */

const CLAVE_COLA = 'sicaqr-cola-registros';
let lector = null;          // lector de QR con la camara (libreria html5-qrcode)
let ultimoTexto = '';
let ultimoMomento = 0;

async function iniciar() {
  await iniciarPagina('registro');

  setInterval(actualizarReloj, 1000);
  actualizarReloj();

  document.querySelectorAll('.pestanas button').forEach(boton =>
    boton.addEventListener('click', () => cambiarPestana(boton.dataset.pestana)));

  document.getElementById('boton-camara').addEventListener('click', activarCamara);
  document.getElementById('boton-detener').addEventListener('click', detenerCamara);
  document.getElementById('boton-token').addEventListener('click', registrarTokenEscrito);
  document.getElementById('token').addEventListener('keydown', e => { if (e.key === 'Enter') registrarTokenEscrito(); });
  document.getElementById('boton-dni').addEventListener('click', registrarPorDni);
  document.getElementById('dni').addEventListener('keydown', e => { if (e.key === 'Enter') registrarPorDni(); });

  // Reenvio automatico de la cola
  window.addEventListener('online', sincronizarCola);
  setInterval(sincronizarCola, 30000);

  mostrarCola();
  await sincronizarCola();
  await cargarRegistradosHoy();
}

function actualizarReloj() {
  const ahora = new Date();
  const hora = ahora.toTimeString().substring(0, 5);
  document.getElementById('reloj').textContent = `${fmtFechaLarga(fechaLocalIso(ahora))} · ${hora}`;
}

function cambiarPestana(nombre) {
  document.querySelectorAll('.pestanas button').forEach(b =>
    b.classList.toggle('activo', b.dataset.pestana === nombre));
  document.getElementById('pestana-qr').hidden = nombre !== 'qr';
  document.getElementById('pestana-dni').hidden = nombre !== 'dni';
  if (nombre === 'dni') {
    detenerCamara();
    document.getElementById('dni').focus();
  }
}

/* -----------------------------------------------------------
   Camara
   ----------------------------------------------------------- */
async function activarCamara() {
  if (typeof Html5Qrcode === 'undefined') {
    avisar('No se pudo cargar el lector de QR', 'error');
    return;
  }
  const contenedor = document.getElementById('lector');
  contenedor.textContent = '';
  lector = new Html5Qrcode('lector');
  try {
    await lector.start(
      { facingMode: 'environment' },               // camara trasera en celulares
      { fps: 10, qrbox: { width: 220, height: 220 } },
      alLeerCodigo,
      () => { /* cuadro sin QR: se ignora */ });
    document.getElementById('boton-camara').disabled = true;
    document.getElementById('boton-detener').disabled = false;
  } catch (error) {
    lector = null;
    contenedor.textContent = 'No se pudo acceder a la cámara';
    avisar('Permita el acceso a la cámara en el navegador o use el campo de código', 'error');
  }
}

async function detenerCamara() {
  if (!lector) return;
  try { await lector.stop(); } catch (e) { /* ya estaba detenida */ }
  lector = null;
  document.getElementById('lector').textContent = 'La cámara está apagada';
  document.getElementById('boton-camara').disabled = false;
  document.getElementById('boton-detener').disabled = true;
}

function alLeerCodigo(texto) {
  // Ignora lecturas repetidas del mismo codigo durante 5 segundos
  const ahora = Date.now();
  if (texto === ultimoTexto && ahora - ultimoMomento < 5000) return;
  ultimoTexto = texto;
  ultimoMomento = ahora;
  registrarToken(texto);
}

function registrarTokenEscrito() {
  const campo = document.getElementById('token');
  const texto = campo.value.trim();
  if (!texto) return;
  campo.value = '';
  registrarToken(texto);
}

/* -----------------------------------------------------------
   Registro por QR
   ----------------------------------------------------------- */
function registrarToken(token) {
  const peticion = {
    token: token,
    capturadoEn: fechaHoraLocalIso(new Date()),   // hora REAL de lectura
    uuidSincronizacion: nuevoUuid()                // identificador unico del envio
  };
  enviarRegistro(peticion);
}

async function enviarRegistro(peticion) {
  try {
    const resultado = await api('/asistencias/qr', 'POST', peticion);
    mostrarResultado(resultado);
    cargarRegistradosHoy();
  } catch (error) {
    if (error instanceof TypeError) {
      // TypeError: sin respuesta del servidor
      guardarEnCola(peticion);
      mostrarPendiente(peticion);
    } else {
      mostrarError(error.message);
    }
  }
}

/* -----------------------------------------------------------
   Registro manual por DNI (RF-10)
   ----------------------------------------------------------- */
async function registrarPorDni() {
  const dni = document.getElementById('dni').value.trim();
  const observacion = document.getElementById('observacion').value.trim();

  if (!/^\d{8}$/.test(dni)) {
    mostrarError('El DNI debe tener 8 dígitos');
    return;
  }
  try {
    const resultado = await api('/asistencias/dni', 'POST', { dni, observacion: observacion || null });
    mostrarResultado(resultado);
    document.getElementById('dni').value = '';
    document.getElementById('observacion').value = '';
    cargarRegistradosHoy();
  } catch (error) {
    mostrarError(error instanceof TypeError ? 'Sin conexión con el servidor. Intente de nuevo.' : error.message);
  }
}

/* -----------------------------------------------------------
   Resultado en pantalla
   ----------------------------------------------------------- */
function mostrarResultado(r) {
  const caja = document.getElementById('resultado');
  caja.className = 'verificacion';
  caja.querySelector('.verificacion-datos').innerHTML = `
    <p class="nombre">${esc(r.nombreCompleto)}</p>
    <p class="seccion">${esc(r.seccion)} · ${esc(r.codigo)}</p>
    ${chipEstado(r.estado, r.hora)}
    <p class="detalle-msg">${esc(r.mensaje)}${r.metodo === 'MANUAL' ? ' · registro manual' : ''}</p>`;
}

function mostrarError(texto) {
  const caja = document.getElementById('resultado');
  caja.className = 'verificacion error';
  caja.querySelector('.verificacion-datos').innerHTML = `
    <p class="nombre">No se registró</p>
    <p class="detalle-msg" style="color:var(--falta)">${esc(texto)}</p>`;
}

function mostrarPendiente(peticion) {
  const caja = document.getElementById('resultado');
  caja.className = 'verificacion';
  caja.querySelector('.verificacion-datos').innerHTML = `
    <p class="nombre">Lectura guardada en este equipo</p>
    <p class="seccion">Leído a las ${peticion.capturadoEn.substring(11, 16)}</p>
    <span class="estado pendiente-chip">Pendiente de envío</span>
    <p class="detalle-msg">Se enviará solo cuando vuelva la conexión, con esta misma hora.</p>`;
}

async function cargarRegistradosHoy() {
  try {
    const hoy = hoyIso();
    const registros = await api(`/asistencias?desde=${hoy}&hasta=${hoy}`);
    document.getElementById('total-hoy').textContent = `${registros.length} registros`;
    document.getElementById('filas-hoy').innerHTML = registros.length
      ? registros.slice(0, 10).map(r => `
          <tr>
            <td>${esc(r.hora)}</td>
            <td>${esc(r.estudiante)}</td>
            <td class="nw">${esc(r.seccion)}</td>
            <td>${chipEstado(r.estado)}</td>
            <td>${esc(r.metodo)}${r.origen === 'SIN_CONEXION' ? ' · sin conexión' : ''}</td>
          </tr>`).join('')
      : filaVacia(5, 'Todavía no hay ingresos registrados hoy.');
  } catch (e) { /* sin conexion: se conserva la ultima lista */ }
}

/* -----------------------------------------------------------
   Cola de registros sin conexion (se guarda en el navegador)
   ----------------------------------------------------------- */
function leerCola() {
  try { return JSON.parse(localStorage.getItem(CLAVE_COLA)) || []; }
  catch (e) { return []; }
}

function escribirCola(cola) {
  try { localStorage.setItem(CLAVE_COLA, JSON.stringify(cola)); }
  catch (e) { /* almacenamiento no disponible */ }
  mostrarCola();
}

function guardarEnCola(peticion) {
  const cola = leerCola();
  cola.push(peticion);
  escribirCola(cola);
}

function mostrarCola() {
  const cantidad = leerCola().length;
  const aviso = document.getElementById('cola');
  aviso.hidden = cantidad === 0;
  aviso.textContent = `${cantidad} ${cantidad === 1 ? 'registro guardado' : 'registros guardados'} en este equipo. ` +
                      'Se enviarán solos cuando vuelva la conexión.';
}

let sincronizando = false;

async function sincronizarCola() {
  if (sincronizando) return;
  let cola = leerCola();
  if (!cola.length) return;

  sincronizando = true;
  let enviados = 0;
  while (cola.length) {
    try {
      await api('/asistencias/qr', 'POST', cola[0]);
      enviados++;
    } catch (error) {
      // Sin conexion o sesion vencida: se reintenta despues
      if (error instanceof TypeError || error.message === 'Sesión vencida') break;
      // Rechazado por una regla de negocio: se descarta de la cola
      avisar('Un registro pendiente no se pudo guardar: ' + error.message, 'error');
    }
    cola.shift();
    escribirCola(cola);
  }
  sincronizando = false;

  if (enviados) {
    avisar(enviados === 1
      ? '1 registro pendiente enviado con su hora de lectura'
      : `${enviados} registros pendientes enviados con su hora de lectura`, 'ok');
    cargarRegistradosHoy();
  }
}

/* Identificador unico para cada lectura (idempotencia, RNF-05) */
function nuevoUuid() {
  if (window.crypto && crypto.randomUUID) return crypto.randomUUID();
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, c => {
    const r = Math.random() * 16 | 0;
    return (c === 'x' ? r : (r & 0x3 | 0x8)).toString(16);
  });
}

iniciar();
