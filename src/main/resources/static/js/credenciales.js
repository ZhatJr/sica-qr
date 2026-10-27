/* ===========================================================
   V03 · Estudiantes y credenciales QR (ADMINISTRADOR)
   API: GET /api/estudiantes · POST /api/credenciales
        PUT /api/credenciales/{id}/revocar · GET /api/credenciales/{token}/qr
   =========================================================== */

let estudiantes = [];
let seleccionado = null;   // idEstudiante elegido en la tabla

async function iniciar() {
  await iniciarPagina('credenciales');
  await cargarSecciones('seccion');

  document.getElementById('seccion').addEventListener('change', mostrarTabla);
  document.getElementById('estado').addEventListener('change', mostrarTabla);
  document.getElementById('buscar').addEventListener('input', mostrarTabla);
  document.getElementById('boton-imprimir').addEventListener('click', imprimir);

  await cargarEstudiantes();
}

async function cargarEstudiantes() {
  estudiantes = await api('/estudiantes');
  const activas = estudiantes.filter(e => e.token).length;
  document.getElementById('subtitulo').textContent =
    `${estudiantes.length} estudiantes · ${activas} credenciales activas`;
  mostrarTabla();
  if (seleccionado) mostrarVista(seleccionado);
}

/* Filtros aplicados en el navegador */
function filtrados() {
  const select = document.getElementById('seccion');
  const seccion = select.value ? select.options[select.selectedIndex].text : '';
  const estado = document.getElementById('estado').value;
  const texto = document.getElementById('buscar').value.trim().toLowerCase();

  return estudiantes.filter(e =>
    (!seccion || e.seccion === seccion) &&
    (!estado || (estado === 'activa' ? e.token : !e.token)) &&
    (!texto || (e.nombreCompleto + ' ' + e.codigo + ' ' + e.dni).toLowerCase().includes(texto)));
}

function mostrarTabla() {
  const lista = filtrados();
  document.getElementById('filas').innerHTML = lista.length
    ? lista.map(e => `
        <tr class="clicable ${e.idEstudiante === seleccionado ? 'seleccionada' : ''}" data-id="${e.idEstudiante}">
          <td>${esc(e.nombreCompleto)}<br><small class="canal">${esc(e.codigo)}</small></td>
          <td>${esc(e.dni)}</td>
          <td class="nw">${esc(e.seccion)}</td>
          <td>${e.emitidaEn ? fmtFecha(e.emitidaEn) : '—'}</td>
          <td>${e.token ? chipEstado('ACTIVA') : '<span class="estado pendiente-chip">Sin credencial</span>'}</td>
        </tr>`).join('')
    : filaVacia(5);

  document.querySelectorAll('#filas tr[data-id]').forEach(fila =>
    fila.addEventListener('click', () => {
      seleccionado = Number(fila.dataset.id);
      mostrarTabla();
      mostrarVista(seleccionado);
    }));
}

/* Vista previa del QR y acciones del estudiante seleccionado */
function mostrarVista(idEstudiante) {
  const e = estudiantes.find(x => x.idEstudiante === idEstudiante);
  const vista = document.getElementById('vista');

  if (!e.token) {
    vista.innerHTML = `
      <strong style="font-size:14px">Vista previa</strong>
      <div class="sin-qr">Sin credencial activa</div>
      <strong style="font-size:13.5px">${esc(e.nombreCompleto)}</strong>
      <p class="canal">${esc(e.seccion)}</p>
      <div class="acciones">
        <button class="boton-principal" id="boton-emitir">Emitir credencial</button>
      </div>`;
    document.getElementById('boton-emitir').addEventListener('click', () => emitir(e.idEstudiante));
    return;
  }

  vista.innerHTML = `
    <strong style="font-size:14px">Vista previa</strong>
    <img src="/api/credenciales/${encodeURIComponent(e.token)}/qr" alt="Código QR de la credencial">
    <strong style="font-size:13.5px">${esc(e.nombreCompleto)}</strong>
    <p class="canal">${esc(e.seccion)}</p>
    <p class="token">${esc(e.token)}</p>
    <p class="leyenda">
      El código contiene solo este identificador aleatorio.
      No incluye el nombre, el documento ni la sección del estudiante (Ley N.° 29733).
    </p>
    <div class="acciones">
      <button class="boton-secundario" id="boton-imprimir-uno">Imprimir esta credencial</button>
      <button class="boton-peligro" id="boton-revocar">Revocar credencial</button>
    </div>
    <div class="form-revocar" id="form-revocar" hidden>
      <label for="motivo">Motivo de la revocación</label>
      <input id="motivo" type="text" maxlength="200" placeholder="Ej.: pérdida reportada por el apoderado">
      <div class="fila-botones">
        <button class="boton-peligro" id="boton-confirmar">Confirmar</button>
        <button class="boton-secundario" id="boton-cancelar">Cancelar</button>
      </div>
    </div>`;

  document.getElementById('boton-imprimir-uno').addEventListener('click', () => imprimir([e]));
  document.getElementById('boton-revocar').addEventListener('click', () => {
    document.getElementById('form-revocar').hidden = false;
    document.getElementById('motivo').focus();
  });
  document.getElementById('boton-cancelar').addEventListener('click', () => {
    document.getElementById('form-revocar').hidden = true;
  });
  document.getElementById('boton-confirmar').addEventListener('click', () => revocar(e));
}

async function emitir(idEstudiante) {
  try {
    await api('/credenciales', 'POST', { idEstudiante });
    avisar('Credencial emitida', 'ok');
    await cargarEstudiantes();
  } catch (error) {
    avisar(error.message, 'error');
  }
}

async function revocar(e) {
  const motivo = document.getElementById('motivo').value.trim();
  if (!motivo) {
    avisar('Indique el motivo de la revocación', 'error');
    return;
  }
  try {
    await api(`/credenciales/${e.idCredencial}/revocar`, 'PUT', { motivo });
    avisar('Credencial revocada. Ya no funcionará en el registro de ingreso.', 'ok');
    await cargarEstudiantes();
  } catch (error) {
    avisar(error.message, 'error');
  }
}

/* Imprime las credenciales activas de la lista filtrada (o una sola) */
function imprimir(lista) {
  const conCredencial = (Array.isArray(lista) ? lista : filtrados()).filter(e => e.token);
  if (!conCredencial.length) {
    avisar('No hay credenciales activas para imprimir', 'error');
    return;
  }

  const zona = document.getElementById('zona-impresion');
  zona.innerHTML = conCredencial.map(e => `
    <div class="tarjeta-impresa">
      <small>I.E. N.° 14762 · SICA-QR</small><br>
      <img src="/api/credenciales/${encodeURIComponent(e.token)}/qr" alt="">
      <strong>${esc(e.nombreCompleto)}</strong>
      <small>${esc(e.seccion)} · ${esc(e.codigo)}</small>
    </div>`).join('');

  // Imprime cuando terminan de cargar las imagenes
  const imagenes = [...zona.querySelectorAll('img')];
  Promise.all(imagenes.map(img => img.complete ? null : new Promise(r => { img.onload = img.onerror = r; })))
    .then(() => window.print());
}

iniciar();
