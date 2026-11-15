# Conversión y paquete de evidencia P06

El PDF se generó **a partir del DOCX** con Pandoc 3.11. Como este entorno no dispone de motor LaTeX, Pandoc convirtió el documento a HTML autónomo con las imágenes incrustadas y Chrome imprimió ese HTML en formato PDF A4. `pdf.css` fija márgenes, tablas y saltos de página para mantener juntos las capturas y sus pies.

Desde la raíz del repositorio:

```bash
pandoc docs/P06/P06_EQUIPO_DSW-E06.docx -f docx -t html5 \
  --standalone --embed-resources --css=docs/P06/pdf.css \
  --metadata=title:P06_EQUIPO_DSW-E06 \
  -o /tmp/P06_EQUIPO_DSW-E06_print.html

google-chrome --headless --no-sandbox --disable-gpu \
  --disable-dev-shm-usage --no-pdf-header-footer \
  --user-data-dir=/tmp/pr06-p06-pdf-chrome \
  --print-to-pdf="$PWD/docs/P06/P06_EQUIPO_DSW-E06.pdf" \
  file:///tmp/P06_EQUIPO_DSW-E06_print.html
```

El resultado se comprobó con `pdfinfo`, `pdftotext` y `pdfimages`: siete páginas A4, tres capturas incorporadas y declaración de IA completa en la última página. `SHA256SUMS.txt` permite comprobar los archivos incluidos en `P06_EQUIPO_DSW-E06_EVIDENCIAS.zip` con `sha256sum -c docs/P06/SHA256SUMS.txt` después de extraerlo en la raíz del proyecto.
